# intuit-subscription-service
Subscription service which will check if the user is subscribed and then validates the profile request

This Service subscribes to RabbitMQ queue to get the request and then checkes the database for the list of products to which the customer is subscribed to.
Once it gets that, it then invokes each of those product's validation endpoint.
Upon getting response from all the endpoints, it sends the response back to profile service through RabbitMQ Topic

VM Args -
java -Dlogsdir=<log_path> -jar <base_path>\intuit-subscription-service\target\intuit-subscription-service-1.0.0-SNAPSHOT.jar
