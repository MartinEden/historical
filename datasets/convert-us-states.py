import json

with open("us-states.geojson") as f:
    data = json.load(f)

for state in data["features"]:
    props = state["properties"]
    state["properties"] = { "ADMIN": props["NAME"] }

with open("us-states-converted.geojson", "w") as f:
    json.dump(data, f)