package mapper.date

import org.scalatest.FunSuite

class DateMapper$Test extends FunSuite {

  val jsonString = "{\"data\":{\"deviceId\":\"11c1310e-c0c2-461b-a4eb-f6bf8da2d23c\",\"temperature\":12,\"location\":{\"latitude\":\"52.14691120000001\",\"longitude\":\"11.658838699999933\"},\"time\":\"1509793231\"}}"

  test("Test map date to human readable format") {
    val result: Record = DateMapper.mapRecord(jsonString)
    assert(result != null)
    assert(result.data != null)
    assert(result.data.deviceId === "11c1310e-c0c2-461b-a4eb-f6bf8da2d23c")
    assert(result.data.temperature === 12)
    assert(result.data.location != null)
    assert(result.data.location.latitude === "52.14691120000001")
    assert(result.data.location.longitude === "52.14691120000001")
    assert(result.data.time === "2017-11-04T13:00:31+02:00")
  }
}
