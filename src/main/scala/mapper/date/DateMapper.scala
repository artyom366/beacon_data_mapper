package mapper.date

import java.util.UUID

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import play.api.libs.json.Json

object DateMapper {

  def main(args: Array[String]) = {

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> s"${UUID.randomUUID().toString}",
      "auto.offset.reset" -> "earliest",
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )

    val conf = new SparkConf().setMaster("local[4]").setAppName("NetworkWordCount")
    val streamingContext = new StreamingContext(conf, Seconds(1))
    val topics = Array("test1", "test2", "test3")

    val stream = KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](topics, kafkaParams))

    stream.print()

    stream.map {
      record => {
        val key = record.key()
        val value = record.value()
        mapDate(value)
      }
    }

    streamingContext.start()
    streamingContext.awaitTermination()

  }

  def mapDate(value: String): Unit = {
    val recordObject = Json.parse(value)
    val record: Record = recordObject.as[Record]
    print(record.data.time)
  }


}
