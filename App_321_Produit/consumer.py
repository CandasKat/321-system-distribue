import pika
import json
import logging
import time
from database import SessionLocal
from crud import update_product_quantity, delete_product, get_product, decrease_product_quantity, increase_product_quantity

# Configure logging
logging.basicConfig(level=logging.INFO, 
                    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

def callback(ch, method, properties, body):
    logger.info(f"Received message: {body}")
    try:
        data = json.loads(body)

        # Handle different field naming conventions (camelCase vs snake_case)
        action = data.get("action") or data.get("Action") or data.get("actionType") or data.get("type")
        produit_id = data.get("produit_id") or data.get("productId") or data.get("id") or data.get("productID")
        quantite = data.get("quantite") or data.get("quantity") or data.get("stock") or data.get("amount")
        commande_id = data.get("commande_id") or data.get("orderId") or data.get("orderID") or 0

        # Convert action to uppercase for case-insensitive comparison
        if action:
            action = action.upper()

        logger.info(f"Processing message: Action={action}, ProductID={produit_id}, Quantity={quantite}, OrderID={commande_id}")

        db = SessionLocal()
        try:
            # Handle different action names
            if action == "CREATED":
                logger.info(f"Decreasing stock for product ID={produit_id} by {quantite}")
                product = decrease_product_quantity(db, produit_id, quantite)
                if product:
                    logger.info(f"Stock decreased for product ID={produit_id}, new stock={product.quantite_en_stock}")
                else:
                    logger.warning(f"Product ID={produit_id} not found for stock decrease")
            elif action == "UPDATED":
                logger.info(f"Updating stock for product ID={produit_id} to {quantite}")
                product = update_product_quantity(db, produit_id, quantite)
                if product:
                    logger.info(f"Stock updated for product ID={produit_id}, new stock={product.quantite_en_stock}")
                else:
                    logger.warning(f"Product ID={produit_id} not found for stock update")
            elif action == "DELETED":
                logger.info(f"Restoring stock for product ID={produit_id} by {quantite}")
                product = get_product(db, produit_id)
                if product:
                    # Restore stock when a delete message is received
                    increase_product_quantity(db, produit_id, quantite)
                    logger.info(f"Stock restored for product ID={produit_id}, new stock={product.quantite_en_stock + quantite}")
                else:
                    logger.warning(f"Product ID={produit_id} not found for stock restoration")
            else:
                logger.warning(f"Unknown action: {action}")

            logger.info(f"Acknowledging message: {method.delivery_tag}")
            ch.basic_ack(delivery_tag=method.delivery_tag)
        finally:
            db.close()
    except Exception as e:
        logger.error(f"Error processing message: {e}", exc_info=True)
        # Still acknowledge the message to prevent it from being requeued
        logger.info(f"Acknowledging message despite error: {method.delivery_tag}")
        ch.basic_ack(delivery_tag=method.delivery_tag)

def consume():
    # Try to connect to RabbitMQ using different hostnames
    hosts = ["rabbitmq", "localhost", "127.0.0.1"]
    connection = None
    max_retries = 5
    retry_count = 0

    while retry_count < max_retries and connection is None:
        for host in hosts:
            try:
                logger.info(f"Attempt {retry_count + 1}/{max_retries}: Connecting to RabbitMQ at {host}...")
                credentials = pika.PlainCredentials('guest', 'guest')
                params = pika.ConnectionParameters(
                    host=host,
                    port=5672,
                    virtual_host='/',
                    credentials=credentials,
                    connection_attempts=3,
                    retry_delay=5,
                    socket_timeout=15
                )
                connection = pika.BlockingConnection(params)
                logger.info(f"Successfully connected to RabbitMQ at {host}")
                break
            except Exception as e:
                logger.warning(f"Failed to connect to RabbitMQ at {host}: {e}")
                if host == hosts[-1]:  # If this was the last host in the list
                    logger.error(f"Could not connect to RabbitMQ after trying all hosts in attempt {retry_count + 1}")

        if connection is None:
            retry_count += 1
            if retry_count < max_retries:
                logger.info(f"Retrying in 10 seconds... (Attempt {retry_count + 1}/{max_retries})")
                time.sleep(10)
            else:
                raise Exception(f"Could not connect to RabbitMQ after {max_retries} attempts")

    logger.info("Setting up RabbitMQ channel and exchanges")
    channel = connection.channel()

    # Original configuration for internal communication
    logger.info("Declaring product_exchange and bindings")
    channel.exchange_declare(exchange='product_exchange', exchange_type='direct', durable=True)
    channel.queue_declare(queue='product_queue', durable=True)
    channel.queue_bind(exchange='product_exchange', queue='product_queue', routing_key='product.update')

    # Additional configuration for Java service (Spring Boot)
    logger.info("Declaring amq.direct exchange and bindings")
    channel.exchange_declare(exchange='amq.direct', exchange_type='direct', durable=True)
    channel.queue_declare(queue='product_queue', durable=True)
    channel.queue_bind(exchange='amq.direct', queue='product_queue', routing_key='product.update')

    # Common Spring Boot default exchange
    logger.info("Declaring amq.topic exchange and bindings")
    channel.exchange_declare(exchange='amq.topic', exchange_type='topic', durable=True)
    channel.queue_bind(exchange='amq.topic', queue='product_queue', routing_key='product.update')
    channel.queue_bind(exchange='amq.topic', queue='product_queue', routing_key='product.#')

    logger.info("Setting QoS and registering consumer")
    channel.basic_qos(prefetch_count=1)
    channel.basic_consume(queue='product_queue', on_message_callback=callback)

    logger.info('Consumer started. Waiting for messages...')
    channel.start_consuming()

if __name__ == "__main__":
    consume()
