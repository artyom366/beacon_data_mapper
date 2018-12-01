package mapper.date

import java.text.SimpleDateFormat
import java.util.UUID

import org.apache.hadoop.hbase._
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.mapreduce.Job
import org.apache.kafka.common.TopicPartition
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

    val hBaseConf = HBaseConfiguration.create()
    hBaseConf.set("hbase.zookeeper.quorum", "localhost")
    hBaseConf.set("hbase.zookeeper.property.clientPort", "2182")

    val hBaseJob = Job.getInstance(hBaseConf)
    val jobConf = hBaseJob.getConfiguration

    jobConf.set(TableOutputFormat.OUTPUT_TABLE, "record")
    hBaseJob.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])

    val sparkConf = new SparkConf().setMaster("local[4]").setAppName("Beacon Data Mapper").set("spark.hbase.host", "localhost")
    val streamingContext = new StreamingContext(sparkConf, Seconds(1))
    val topics = Array("test1", "test2", "test3")
    val offsets = Map(new TopicPartition("test1", 0) -> 0L, new TopicPartition("test2", 0) -> 0L, new TopicPartition("test3", 0) -> 0L)

    val stream = KafkaUtils.createDirectStream[String, String](streamingContext, PreferConsistent, Subscribe[String, String](topics, kafkaParams, offsets))

    stream.foreachRDD(rdd => {
      if (!rdd.isEmpty())
        rdd.map(record => mapRecord(record.value())).map(record => convertToPut(record)).saveAsNewAPIHadoopDataset(jobConf)
      //rdd.map(record => mapRecord(record.value())).saveAsTextFile("/home/artyom/hdfs_files/")
    })

    streamingContext.start()
    streamingContext.awaitTermination()
  }

  def mapRecord(value: String): Record = {
    val recordObject = Json.parse(value)
    val record: Record = recordObject.as[Record]
    val location: Location = Location(record.data.location.latitude, record.data.location.latitude)
    val data: Data = Data(record.data.deviceId, record.data.temperature, location, convertDate(record.data.time))
    Record(data)
  }

  def convertDate(timeStamp: String): String = {
    val correctedTimeStamp = correctTimeStamp(timeStamp)
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
    dateFormat.format(correctedTimeStamp)
  }

  def correctTimeStamp(timeStamp: String): Long = {
    timeStamp.toLong * 1000L
  }

  def convertToPut(record: Record): (ImmutableBytesWritable, Put) = {
    val uuid: String = UUID.randomUUID().toString
    val put = new Put(Bytes.toBytes(uuid))
    put.add(createCell(uuid, String.valueOf(record.data.deviceId), "D", "Id"))
    put.add(createCell(uuid, String.valueOf(record.data.temperature), "T", "Temp"))
    put.add(createCell(uuid, String.valueOf(record.data.time), "T", "Time"))
    put.add(createCell(uuid, String.valueOf(record.data.location.latitude), "L", "Lat"))
    put.add(createCell(uuid, String.valueOf(record.data.location.longitude), "L", "Lon"))
    (new ImmutableBytesWritable(Bytes.toBytes(uuid)), put)
  }

  def createCell(rowId: String, value: String, family: String, qualifier: String): Cell = {
    createCell(Bytes.toBytes(rowId), Bytes.toBytes(value), Bytes.toBytes(family), Bytes.toBytes(qualifier))
  }

  def createCell(rowId: Array[Byte], value: Array[Byte], family: Array[Byte], qualifier: Array[Byte]): Cell = {
    val cellBuilder: CellBuilder = CellBuilderFactory.create(CellBuilderType.SHALLOW_COPY)
    cellBuilder.setRow(rowId)
    cellBuilder.setValue(value)
    cellBuilder.setFamily(family)
    cellBuilder.setQualifier(qualifier)
    cellBuilder.setType(Cell.Type.Put)
    cellBuilder.build()
  }
}
