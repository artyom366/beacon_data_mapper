package mapper.date

case class Record(data: Data, time: String)

case class Data(deviceId: String, temperature: Int, location: Location)

case class Location(latitude: String, longitude: String)

