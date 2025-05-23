from sqlalchemy import Column, Integer, String, Float
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()

class Product(Base):
    __tablename__ = 'products'

    id = Column(Integer, primary_key=True, index=True)
    nom = Column(String, index=True)
    description = Column(String)
    prix = Column(Float)
    quantite_en_stock = Column(Integer)
