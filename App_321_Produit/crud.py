from sqlalchemy.orm import Session
from models import Product
from schemas import ProductCreate

def create_product(db: Session, product: ProductCreate):
    db_product = Product(
        nom=product.nom,
        description=product.description,
        prix=product.prix,
        quantite_en_stock=product.quantite_en_stock
    )
    db.add(db_product)
    db.commit()
    db.refresh(db_product)
    return db_product

def get_products(db: Session, skip: int = 0, limit: int = 10):
    return db.query(Product).offset(skip).limit(limit).all()

def get_product(db: Session, product_id: int):
    return db.query(Product).filter(Product.id == product_id).first()

def update_product_quantity(db: Session, product_id: int, quantite: int):
    product = get_product(db, product_id)
    if product:
        product.quantite_en_stock = quantite
        db.commit()
        db.refresh(product)
    return product

def decrease_product_quantity(db: Session, product_id: int, quantite: int):
    product = get_product(db, product_id)
    if product:
        product.quantite_en_stock -= quantite
        db.commit()
        db.refresh(product)
    return product

def increase_product_quantity(db: Session, product_id: int, quantite: int):
    product = get_product(db, product_id)
    if product:
        product.quantite_en_stock += quantite
        db.commit()
        db.refresh(product)
    return product

def delete_product(db: Session, product: Product):
    db.delete(product)
    db.commit()

