@echo off
REM Start the consumer in the background
start /b python consumer.py

REM Start the FastAPI application
uvicorn main:app --host 0.0.0.0 --port 8000