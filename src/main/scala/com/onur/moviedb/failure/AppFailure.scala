package com.onur.moviedb.failure

trait FailureMsg {
  def failMsg: String

  def failReason: String = {
    this.getClass.getSimpleName
  }

  def fullMsg: String = {
    failReason + " " + failMsg
  }
}

trait OperationFailure extends FailureMsg with Product with Serializable
trait ValidationFailure extends FailureMsg with Product with Serializable
