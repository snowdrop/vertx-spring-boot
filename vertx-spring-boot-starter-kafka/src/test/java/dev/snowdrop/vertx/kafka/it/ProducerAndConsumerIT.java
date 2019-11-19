//package dev.snowdrop.vertx.kafka.it;
//
//import dev.snowdrop.vertx.kafka.KafkaConsumer;
//import dev.snowdrop.vertx.kafka.KafkaConsumerFactory;
//import dev.snowdrop.vertx.kafka.KafkaProducer;
//import dev.snowdrop.vertx.kafka.KafkaProducerFactory;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.junit4.SpringRunner;
//import reactor.core.publisher.Mono;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(properties = {
//    "vertx.kafka.producer.key.serializer=org.apache.kafka.common.serialization.StringSerializer",
//    "vertx.kafka.producer.value.serializer=org.apache.kafka.common.serialization.StringSerializer",
//    "vertx.kafka.consumer.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
//    "vertx.kafka.consumer.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
//    "vertx.kafka.consumer.group.id=test_group"
//})
//@EmbeddedKafka(topics = "test", partitions = 1, bootstrapServersProperty = "vertx.kafka.producer.bootstrap.servers")
//public class ProducerAndConsumerIT {
//
//    @Autowired
//    private KafkaProducerFactory producerFactory;
//
//    @Autowired
//    private KafkaConsumerFactory consumerFactory;
//
//    private KafkaProducer<String, String> producer;
//
//    private KafkaConsumer<String, String> consumer;
//
//    @Before
//    public void setUp() {
//        producer = producerFactory.create();
//        consumer = consumerFactory.create();
//    }
//
//    @After
//    public void tearDown() {
//        Mono.zip(producer.close(), consumer.close()).block();
//    }
//
////    @Test
//    public void shouldSendAndReceive() throws InterruptedException {
////        consumer.assign(KafkaTopicPartition.create("t1", 0)).block();
////        consumer.seekToBeginning(KafkaTopicPartition.create("t1", 0)).block();
////        consumer.subscribe("t1").block();
//
////        StepVerifier.create(consumer.subscriptions())
////            .expectNext("t1")
////            .verifyComplete();
////
////        List<KafkaConsumerRecord<String, String>> records = new CopyOnWriteArrayList<>();
////        Disposable disposable = consumer.flux()
////            .subscribe(record -> {
////                System.out.println("received " + record);
////                records.add(record);
////            });
////
//////        Thread.sleep(2000);
////
////        Mono.zip(
////            producer.send(KafkaProducerRecord.builder("t1", "v1", String.class).build()),
////            producer.send(KafkaProducerRecord.builder("t1", "v2", String.class).build()),
////            producer.send(KafkaProducerRecord.builder("t1", "v3", String.class).build())
////        ).block();
////
////        await().atMost(5, TimeUnit.SECONDS)
////            .until(() -> records.size() == 3);
////        disposable.dispose();
//
////        StepVerifier.create(consumer.flux())
////            .assertNext(record -> assertThat(record.value()).isEqualTo("v1"))
////            .assertNext(record -> assertThat(record.value()).isEqualTo("v2"))
////            .assertNext(record -> assertThat(record.value()).isEqualTo("v3"))
////            .thenCancel()
////            .verify();
//    }
//}
