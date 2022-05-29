import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.ConfigResource.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleAdmin {

    private final static Logger logger = LoggerFactory.getLogger(SimpleAdmin.class);
    private final static String BOOTSTRAP_SERVERS = "54.177.38.197:9092";

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Properties configs = new Properties();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        AdminClient adminClient = AdminClient.create(configs);

        logger.info("== Get Broker Information");
        for (Node node : adminClient.describeCluster().nodes().get()) {
            logger.info("node : {}", node);
            ConfigResource configResource = new ConfigResource(Type.BROKER, node.idString());
            DescribeConfigsResult describeConfigsResult = adminClient.describeConfigs(
                Collections.singleton(configResource)
            );
            describeConfigsResult.all().get().forEach((broker, config) -> {
                config.entries().forEach(
                    configEntry -> logger.info(configEntry.name() + "= " + configEntry.value())
                );
            });
        }

        Map<String, TopicDescription> topicDescriptionMap = adminClient.describeTopics(
            Collections.singletonList("test")).all().get();
        logger.info("{}", topicDescriptionMap);

        adminClient.close();
    }
}
