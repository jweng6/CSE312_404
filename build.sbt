name := """cse312_project"""
organization := "chuan"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.10"

libraryDependencies += guice
libraryDependencies += "com.alibaba" % "fastjson" % "1.2.59"

