package models

import scala.collection.mutable.HashMap
import akka.actor.{ TypedActor, TypedProps }
import play.api.libs.concurrent.Akka // access to Akka.system
import play.api.Play.current // implicit argument for Akka.system

/**
 * A repository for simple tasks.
 */
trait TaskRepository {
  def all(): List[Task]
  def create(label: String): Unit
  def delete(id: Long): Unit
}

/**
 * Actor-based thread-safe wrapper around an in-memory task repo.
 */
class TaskRepositoryInMemory extends TaskRepository {
  val repo: TaskRepository =
    TypedActor(Akka.system).typedActorOf(TypedProps[TaskRepositoryInMemoryImpl]())
  def all() = repo.all()
  def create(label: String) = repo.create(label)
  def delete(id: Long) = repo.delete(id)
}

/**
 * A stateful, non-thread-safe, in-memory implementation
 * of a repository for simple tasks. This is not accessible
 * outside of this file.
 */
private class TaskRepositoryInMemoryImpl extends TaskRepository {
  val tasks = new HashMap[Long, Task]
  var id = 0L

  def nextId() = { id += 1 ; id }
  def all() = tasks.values.toList
  def create(label: String) { val id = nextId() ; tasks += id -> Task(id, label) }
  def delete(id: Long) { tasks -= id }
}