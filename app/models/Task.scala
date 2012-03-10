package models

case class Task(id: Long, label: String)

object Task extends TaskRepositoryInMemory
