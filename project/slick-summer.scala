import sbt._
import Keys._
import cn.gov.heshan.sbt.CustomSettings
import com.typesafe.sbt.SbtGit._

object enuma extends Build {
  
  val initPrintln = """
 ___  _   _  _ __ ___   _ __ ___    ___  _ __ 
/ __|| | | || '_ ` _ \ | '_ ` _ \  / _ \| '__|
\__ \| |_| || | | | | || | | | | ||  __/| |   
|___/ \__,_||_| |_| |_||_| |_| |_| \___||_|   

"""
  println(initPrintln)

  lazy val `slick-summer` = (project in file("."))
  .settings(CustomSettings.customSettings: _*)
  .settings(
    name := "slick-summer",
    libraryDependencies ++= Seq(
      "com.typesafe.slick" %% "slick" % "3.0.0"
    )
  )

}
