package mapper.date

import java.text.SimpleDateFormat
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
        val mappedRecord: Record = mapRecord(value)
      }
    }

    streamingContext.start()
    streamingContext.awaitTermination()

  }

  def mapRecord(value: String): Record = {
    val recordObject = Json.parse(value)
    val record: Record = recordObject.as[Record]
    val location: Location = new Location(record.data.location.latitude, record.data.location.latitude)
    val data: Data = new Data(record.data.deviceId, record.data.temperature, location, convertDate(record.data.time))
    new Record(data)
  }

  def convertDate(timeStamp: String): String = {
    val correctedTimeStamp = correctTimeStamp(timeStamp)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    dateFormat.format(correctedTimeStamp)
  }

  def correctTimeStamp(timeStamp: String): Long = {
    timeStamp.toLong * 1000L
  }
}
