from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session
from database import engine, get_db, SessionLocal
import schemas
import models
import crud
from publisher import publish_message
import logging

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Create tables in the PostgreSQL database
try:
    logger.info("Creating database tables if they don't exist")
    models.Base.metadata.create_all(bind=engine)
    logger.info("Database tables created successfully")
except Exception as e:
    logger.error(f"Error creating database tables: {e}")

# Test database connection
try:
    db = SessionLocal()
    db.execute("SELECT 1")
    logger.info("Database connection successful")
    db.close()
except Exception as e:
    logger.error(f"Database connection failed: {e}")

app = FastAPI()

@app.post("/produits/")
def create_product(product: schemas.ProductCreate, db: Session = Depends(get_db)):
    db_product = crud.create_product(db, product)
    publish_message({
        "action": "CREATED",
        "produit_id": db_product.id,
        "quantite": db_product.quantite_en_stock,
        "commande_id": 0
    })
    return db_product

@app.get("/produits", response_model=list[schemas.Product])
def read_products(skip: int = 0, limit: int = 10, db: Session = Depends(get_db)):
    return crud.get_products(db, skip=skip, limit=limit)

@app.get("/produits/{product_id}")
def get_product(product_id: int, db: Session = Depends(get_db)):
    product = crud.get_product(db, product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Produit non trouvé")
    return product

@app.put("/produits/{product_id}")
def update_product(product_id: int, product_data: schemas.ProductUpdate, db: Session = Depends(get_db)):
    db_product = crud.get_product(db, product_id)
    if not db_product:
        raise HTTPException(status_code=404, detail="Produit non trouvé")
    for key, value in product_data.dict(exclude_unset=True).items():
        setattr(db_product, key, value)
    db.commit()
    db.refresh(db_product)
    return db_product

@app.delete("/produits/{product_id}")
def delete_product(product_id: int, db: Session = Depends(get_db)):
    db_product = crud.get_product(db, product_id)
    if not db_product:
        raise HTTPException(status_code=404, detail="Produit non trouvé")

    crud.delete_product(db, db_product)
    return {"message": "Produit supprimé avec succès"}
