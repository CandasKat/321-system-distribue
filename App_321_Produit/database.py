from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
import os

# Get database connection parameters from environment variables or use defaults
DB_HOST = os.getenv("POSTGRES_HOST", "postgres")
DB_PORT = os.getenv("POSTGRES_PORT", "5432")
DB_NAME = os.getenv("POSTGRES_DB", "commandes")
DB_USER = os.getenv("POSTGRES_USER", "candas")
DB_PASSWORD = os.getenv("POSTGRES_PASSWORD", "2003")

DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}"

engine = create_engine(DATABASE_URL)

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
