from flask import Flask, request, jsonify
from database import get_db_connection
import os
import sys

# Connect YOLO detection module
sys.path.append("../ai/road damage")
from detect import detect_damage

app = Flask(__name__)


@app.route("/")
def home():
    return "Road Damage Detection System Backend is Running!"


# =========================
# CITIZEN REGISTER
# =========================
@app.route("/register", methods=["POST"])
def register():
    data = request.get_json()

    name = data["name"]
    email = data["email"]
    contact_no = data["contact_no"]
    password = data["password"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO citizen
    (name, email, contact_no, password, registration_date)
    VALUES (%s, %s, %s, %s, CURDATE())
    """

    cursor.execute(
        query,
        (name, email, contact_no, password)
    )

    connection.commit()

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Citizen registered successfully!"
    }), 201


# =========================
# CITIZEN LOGIN
# =========================
@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()

    email = data["email"]
    password = data["password"]

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    cursor.execute(
        "SELECT * FROM citizen WHERE email = %s AND password = %s",
        (email, password)
    )

    citizen = cursor.fetchone()

    cursor.close()
    connection.close()

    if citizen:
        return jsonify({
            "message": "Login successful!",
            "citizen": citizen
        }), 200
    else:
        return jsonify({
            "message": "Invalid email or password!"
        }), 401


# =========================
# CREATE COMPLAINT + YOLO
# =========================
@app.route("/complaint", methods=["POST"])
def create_complaint():

    citizen_id = request.form["citizen_id"]
    location_id = request.form["location_id"]
    description = request.form["description"]

    image = request.files.get("image")

    image_path = None

    # -------------------------
    # Save uploaded image
    # -------------------------
    if image and image.filename != "":

        filename = image.filename

        upload_folder = os.path.join(
            os.path.dirname(__file__),
            "uploads"
        )

        # Create uploads folder if it doesn't exist
        os.makedirs(upload_folder, exist_ok=True)

        image_path = os.path.join(
            upload_folder,
            filename
        )

        image.save(image_path)

    # -------------------------
    # Insert complaint
    # -------------------------
    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO complaint
    (citizen_id, location_id, complaint_date, status, description, image_path)
    VALUES (%s, %s, NOW(), %s, %s, %s)
    """

    cursor.execute(
        query,
        (
            citizen_id,
            location_id,
            "Pending",
            description,
            image_path
        )
    )

    connection.commit()

    complaint_id = cursor.lastrowid

    cursor.close()
    connection.close()

    # ==================================================
    # YOLO DETECTION
    # ==================================================

    detections = []

    if image_path:

        try:

            detections = detect_damage(image_path)

        except Exception as e:

            print("YOLO Detection Error:", e)

            detections = []

    # ==================================================
    # SAVE YOLO RESULTS INTO road_damage TABLE
    # ==================================================

    damage_records = []

    if detections:

        connection = get_db_connection()
        cursor = connection.cursor()

        for detection in detections:

            damage_type = detection["damage_type"]
            confidence_score = detection["confidence_score"]

            query = """
            INSERT INTO road_damage
            (complaint_id, damage_type, confidence_score, detection_date)
            VALUES (%s, %s, %s, NOW())
            """

            cursor.execute(
                query,
                (
                    complaint_id,
                    damage_type,
                    confidence_score
                )
            )

            damage_id = cursor.lastrowid

            damage_records.append({
                "damage_id": damage_id,
                "damage_type": damage_type,
                "confidence_score": confidence_score
            })

        connection.commit()

        cursor.close()
        connection.close()

    # ==================================================
    # RESPONSE
    # ==================================================

    return jsonify({

        "message": "Complaint submitted successfully!",

        "complaint_id": complaint_id,

        "image_path": image_path,

        "ai_detection": damage_records

    }), 201


# =========================
# MANUAL ROAD DAMAGE
# =========================
@app.route("/road-damage", methods=["POST"])
def add_road_damage():

    data = request.get_json()

    complaint_id = data["complaint_id"]
    damage_type = data["damage_type"]
    confidence_score = data["confidence_score"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO road_damage
    (complaint_id, damage_type, confidence_score, detection_date)
    VALUES (%s, %s, %s, NOW())
    """

    cursor.execute(
        query,
        (
            complaint_id,
            damage_type,
            confidence_score
        )
    )

    connection.commit()

    damage_id = cursor.lastrowid

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Road damage added successfully!",
        "damage_id": damage_id
    }), 201


# =========================
# AUTHORITY REGISTER
# =========================
@app.route("/authority/register", methods=["POST"])
def register_authority():

    data = request.get_json()

    name = data["name"]
    email = data["email"]
    contact_no = data["contact_no"]
    password = data["password"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO municipal_authority
    (name, email, contact_no, password)
    VALUES (%s, %s, %s, %s)
    """

    cursor.execute(
        query,
        (
            name,
            email,
            contact_no,
            password
        )
    )

    connection.commit()

    authority_id = cursor.lastrowid

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Municipal authority registered successfully!",
        "authority_id": authority_id
    }), 201


# =========================
# CREATE REPAIR
# =========================
@app.route("/repair", methods=["POST"])
def create_repair():

    data = request.get_json()

    damage_id = data["damage_id"]
    authority_id = data["authority_id"]

    repair_status = data.get(
        "repair_status",
        "Pending"
    )

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO repair
    (damage_id, authority_id, repair_date, repair_status)
    VALUES (%s, %s, CURDATE(), %s)
    """

    cursor.execute(
        query,
        (
            damage_id,
            authority_id,
            repair_status
        )
    )

    connection.commit()

    repair_id = cursor.lastrowid

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Repair created successfully!",
        "repair_id": repair_id
    }), 201


# =========================
# UPDATE REPAIR STATUS
# =========================
@app.route("/repair/<int:repair_id>", methods=["PUT"])
def update_repair_status(repair_id):

    data = request.get_json()

    repair_status = data["repair_status"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    UPDATE repair
    SET repair_status = %s
    WHERE repair_id = %s
    """

    cursor.execute(
        query,
        (
            repair_status,
            repair_id
        )
    )

    connection.commit()

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Repair status updated successfully!"
    }), 200


# =========================
# CREATE NOTIFICATION
# =========================
@app.route("/notification", methods=["POST"])
def create_notification():

    data = request.get_json()

    complaint_id = data["complaint_id"]
    message = data["message"]
    status = data["status"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    INSERT INTO notification
    (complaint_id, message, status)
    VALUES (%s, %s, %s)
    """

    cursor.execute(
        query,
        (
            complaint_id,
            message,
            status
        )
    )

    connection.commit()

    notification_id = cursor.lastrowid

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Notification created successfully!",
        "notification_id": notification_id
    }), 201


# =========================
# GET ALL COMPLAINTS
# =========================
@app.route("/complaints", methods=["GET"])
def get_complaints():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    query = """
    SELECT * FROM complaint
    """

    cursor.execute(query)

    complaints = cursor.fetchall()

    cursor.close()
    connection.close()

    return jsonify(complaints), 200


# =========================
# GET ALL NOTIFICATIONS
# =========================
@app.route("/notifications", methods=["GET"])
def get_notifications():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    query = """
    SELECT * FROM notification
    """

    cursor.execute(query)

    notifications = cursor.fetchall()

    cursor.close()
    connection.close()

    return jsonify(notifications), 200


# =========================
# GET COMPLAINT BY ID
# =========================

@app.route("/complaint/<int:complaint_id>", methods=["GET"])
def get_complaint_by_id(complaint_id):

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    query = """
    SELECT
        c.complaint_id,
        c.citizen_id,
        c.location_id,
        c.description,
        DATE_FORMAT(c.complaint_date, '%Y-%m-%d') AS complaint_date,
        c.status,
        c.image_path,

        l.address AS location,
        l.latitude,
        l.longitude,

        rd.damage_type,
        rd.confidence_score

    FROM complaint c

    LEFT JOIN location l
        ON c.location_id = l.location_id

    LEFT JOIN road_damage rd
        ON c.complaint_id = rd.complaint_id

    WHERE c.complaint_id = %s

    ORDER BY rd.damage_id DESC

    LIMIT 1
    """

    cursor.execute(query, (complaint_id,))
    complaint = cursor.fetchone()

    cursor.close()
    connection.close()

    if complaint:
        return jsonify(complaint), 200
    else:
        return jsonify({
            "message": "Complaint not found!"
        }), 404
# =========================
# GET REPAIRS
# =========================
@app.route("/repairs", methods=["GET"])
def get_repairs():

    connection = get_db_connection()
    cursor = connection.cursor(dictionary=True)

    query = """
    SELECT * FROM repair
    """

    cursor.execute(query)

    repairs = cursor.fetchall()

    cursor.close()
    connection.close()

    return jsonify(repairs), 200


# =========================
# UPDATE COMPLAINT STATUS
# =========================
@app.route("/complaint/<int:complaint_id>/status", methods=["PUT"])
def update_complaint_status(complaint_id):

    data = request.get_json()

    status = data["status"]

    connection = get_db_connection()
    cursor = connection.cursor()

    query = """
    UPDATE complaint
    SET status = %s
    WHERE complaint_id = %s
    """

    cursor.execute(
        query,
        (
            status,
            complaint_id
        )
    )

    connection.commit()

    cursor.close()
    connection.close()

    return jsonify({
        "message": "Complaint status updated successfully!"
    }), 200


# =========================
# START FLASK SERVER
# =========================
if __name__ == "__main__":
    app.run(debug=True)