package com.onur.moviedb.domain

sealed trait ValidationStatus
sealed trait Validated extends ValidationStatus
sealed trait UnValidated extends ValidationStatus
