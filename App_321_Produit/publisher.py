import pika
import json

def publish_message(message: dict):
    # Try to connect to RabbitMQ using different hostnames
    hosts = ["rabbitmq", "localhost", "127.0.0.1"]
    connection = None

    for host in hosts:
        try:
            print(f"Trying to connect to RabbitMQ at {host}...")
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
            print(f"Successfully connected to RabbitMQ at {host}")
            break
        except Exception as e:
            print(f"Failed to connect to RabbitMQ at {host}: {e}")
            if host == hosts[-1]:  # If this was the last host in the list
                raise Exception(f"Could not connect to RabbitMQ after trying all hosts: {hosts}")

    channel = connection.channel()

    channel.exchange_declare(exchange='product_exchange', exchange_type='direct', durable=True)
    channel.queue_declare(queue='product_queue', durable=True)
    channel.queue_bind(exchange='product_exchange', queue='product_queue', routing_key='product.update')

    channel.basic_publish(
        exchange='product_exchange',
        routing_key='product.update',
        body=json.dumps(message),
        properties=pika.BasicProperties(delivery_mode=2)  # Message persistant
    )
    connection.close()
