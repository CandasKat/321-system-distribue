from pydantic import BaseModel

class ProductBase(BaseModel):
    nom: str
    description: str
    prix: float
    quantite_en_stock: int

    class Config:
        from_attributes = True

class ProductCreate(ProductBase):
    pass

class ProductUpdate(BaseModel):
    nom: str | None = None
    description: str | None = None
    prix: float | None = None
    quantite_en_stock: int | None = None

    class Config:
        from_attributes = True

class Product(ProductBase):
    id: int

    class Config:
        from_attributes = True
