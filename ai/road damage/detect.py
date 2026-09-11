from ultralytics import YOLO
import os

MODEL_PATH = os.path.join(os.path.dirname(__file__), "best.pt")

model = YOLO(MODEL_PATH)


def detect_damage(image_path):
    results = model(image_path)

    detections = []

    for result in results:
        for box in result.boxes:
            class_id = int(box.cls[0])
            confidence = float(box.conf[0])

            damage_type = result.names[class_id]

            detections.append({
                "damage_type": damage_type,
                "confidence_score": confidence
            })

    return detections