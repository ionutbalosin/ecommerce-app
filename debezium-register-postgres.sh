
echo ""
echo "*****************************************************************"
echo "*  Register the PostgreSQL configuration to the Kafka connector *"
echo "*****************************************************************"

curl -i -X POST -H "Accept:application/json"  \
                -H  "Content-Type:application/json" http://localhost:18083/connectors/ \
                -d @debezium-register-postgres.json