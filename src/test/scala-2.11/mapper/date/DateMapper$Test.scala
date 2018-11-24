package mapper.date

import mapper.date.DateMapper.mapDate
import org.scalatest.FunSuite
import play.api.libs.json
import play.api.libs.functional.syntax._
import play.api.libs.json.JsPath

class DateMapper$Test extends FunSuite {

  val jsonString = "{\"data\":{\"deviceId\":\"11c1310e-c0c2-461b-a4eb-f6bf8da2d23c\",\"temperature\":12,\"location\":{\"latitude\":\"52.14691120000001\",\"longitude\":\"11.658838699999933\"},\"time\":\"1509793231\"}}"

  test("") {

      DateMapper.mapDate(jsonString)
  }

}
