import mysql.connector

def get_db_connection():
    connection = mysql.connector.connect(
        host="localhost",
        user="root",
        password="bsammB1234",
        database="road_damage_db"
    )

    return connection