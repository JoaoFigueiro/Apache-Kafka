package org;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.EventHandler;

import okhttp3.Headers;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.StringSerializer;

public class WikimediaChangerProducer {
    public static void main(String[] args) throws InterruptedException {

        String bootstrapServer = "localhost:9092";

        Properties properties = new Properties();

        properties.setProperty("bootstrap.servers", "localhost:9092");

        properties.setProperty("key.serializer", StringSerializer.class.getName());
        properties.setProperty("value.serializer", StringSerializer.class.getName());

        //properties.setProperty("acks", "all");
        //properties.setProperty("enable.idempotence", "true");
        //properties.setProperty("retries", Integer.toString(Integer.MAX_VALUE);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        String topic = "wikimedia.recentchange";

        EventHandler eventHandler = new WikimediaChangeHandler(producer, topic);

        String url = "https://stream.wikimedia.org/v2/stream/recentchange";

        Headers headers = Headers.of("User-Agent", "Kafka-Wikimedia-Client/1.0 (https://example.com)");

        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url)).headers(headers);
        EventSource eventSource = builder.build();

        eventSource.start();

        TimeUnit.MINUTES.sleep(10);

    }
}
