package mapper.date

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads}


case class Data(deviceId: String, temperature: Int, location: Location, time: String)

object Data {
  implicit val dataReads: Reads[Data] = (
    (JsPath \ "deviceId").read[String] and
      (JsPath \ "temperature").read[Int] and
      (JsPath \ "location").read[Location] and
      (JsPath \ "time").read[String]
    ) (Data.apply _)
}

case class Location(latitude: String, longitude: String)

object Location {
  implicit val locationReads: Reads[Location] = (
    (JsPath \ "latitude").read[String] and
      (JsPath \ "longitude").read[String]
    ) (Location.apply _)
}

case class Record(data: Data)

object Record {
  implicit val recordReads: Reads[Record] = (JsPath \ "data").read[Data].map { data => Record(data) }
}

//case class Record(data: Data, ss: Option[String])
//
//object Record {
//  implicit val recordReads: Reads[Record] = (
//    (JsPath \ "data").read[Data] and
//      (JsPath \ "ss").readNullable[String]
//    ) (Record.apply _)
//}
