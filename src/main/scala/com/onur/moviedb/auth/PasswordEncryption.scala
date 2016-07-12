package com.onur.moviedb.auth

import java.security.SecureRandom
import java.util
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

import com.onur.moviedb.domain.Validated
import com.onur.moviedb.failure.ValidationFailure

import scala.util.control.NonFatal
import scalaz.{-\/, \/, \/-}

object PasswordEncryption {
  private val Pbkdf2Algorithm = "PBKDF2WithHmacSHA512"

  // These constants may be changed without breaking existing hashes.
  private val SaltByteSize = 24
  private val HashByteSize = 18
  private val Pbkdf2Iterations = 64000

  // These constants define the encoding and may not be changed.
  private val HashSections  = 5
  private val HashAlgorithmIndex = 0
  private val IterationIndex = 1
  private val HashSizeIndex  = 2
  private val SaltIndex = 3
  private val Pbkdf2Index  = 4
  private val SHA = "sha512"


  def createHash(ctp: ClearTextPassword[Validated]): EncryptedPassword = {
    val random  = new SecureRandom
    val salt = new Array[Byte](SaltByteSize)
    random.nextBytes(salt)
    val pass = ctp.pass.toCharArray
    val hash = pbkdf2(pass, salt, Pbkdf2Iterations, HashByteSize)
    val hashSize: Int = hash.length
    zeroOut(pass)
    val encoder = Base64.getEncoder
    EncryptedPassword(SHA + ":" + Pbkdf2Iterations +
      ":" + hashSize + ":" +
      encoder.encodeToString(salt) + ":" + encoder.encodeToString(hash))
  }

  def verifyPassword(ctp: ClearTextPassword[Validated], ecp: EncryptedPassword): ValidationFailure \/ Boolean = {
    val passChar = ctp.pass.toCharArray
    val res = verifyPassword(passChar, ecp.pass)
    zeroOut(passChar)
    res
  }

  private def verifyPassword(password: Array[Char], encryptedHash: String): ValidationFailure \/ Boolean = {
    // Decode the hash into its parameters
    val hashParts = encryptedHash.split(":")
    val decoder = Base64.getDecoder
    val iterationsRes = \/.fromTryCatchNonFatal(Integer.parseInt(hashParts(IterationIndex))).
      leftMap(_ => InvalidHashFailure("Could not parse the iteration count as an integer"))

    val saltRes = \/.fromTryCatchNonFatal(decoder.decode(hashParts(SaltIndex))).
      leftMap(_ => Base64DecodingSaltFailed("Base64 decoding of salt failed"))

    val hashRes = \/.fromTryCatchNonFatal(decoder.decode(hashParts(Pbkdf2Index))).
      leftMap(_ => Base64DecodingSaltFailed("Base64 decoding of pbkdf2 output failed"))

    val hashSizeRes = \/.fromTryCatchNonFatal(Integer.parseInt(hashParts(HashSizeIndex))).
      leftMap(_ => Base64DecodingSaltFailed("Could not parse the hash size as an integer"))

    if(hashParts.length != HashSections) {
      -\/(InvalidHashFailure("Fields are missing from the password hash"))
    }
    else if(hashParts(HashAlgorithmIndex) != SHA) {
      -\/(InvalidHashFailure("Unsupported hash type"))
    }
    else {
      for{
        iterations <- iterationsRes
        res <- if(iterations >= 1) {
          for {
            salt <- saltRes
            hash <- hashRes
            hashSize <- hashSizeRes
            res <- if(hash.length != hashSize) {
              -\/(InvalidHashFailure("Hash length doesn't match stored hash length."))
            } else {
              // Compute the hash of the provided password, using the same salt,
              // iteration count, and hash length
              val testHash = pbkdf2(password, salt, iterations, hash.length)
              // Compare the hashes in constant timeV. The password is correct if
              // both hashes match.
              \/-(slowEquals(hash, testHash))
            }
          } yield res
        } else {-\/(InvalidHashFailure("Invalid number of iterations. Must be >= 1"))}
      } yield res
    }
  }

  private def pbkdf2(password: Array[Char], salt: Array[Byte], iterations: Int, bytes: Int): Array[Byte] = {
    try {
      val spec: PBEKeySpec = new PBEKeySpec(password, salt, iterations, bytes * 8)
      val skf: SecretKeyFactory = SecretKeyFactory.getInstance(Pbkdf2Algorithm)
      skf.generateSecret(spec).getEncoded
    }
    catch {
      case NonFatal(e) =>
        throw new RuntimeException(e)
    }
  }

  /**
    * Wipe out the unencrypted password from memory for security reasons
    *
    * @param password
    */
  private def zeroOut(password: Array[Char]): Unit = util.Arrays.fill(password, '0')

  private def slowEquals(xs: Array[Byte], ys: Array[Byte]): Boolean = {
    val diff: Int = xs.length ^ ys.length
    (xs,ys).zipped.foldLeft(diff){case (acc, (x, y)) => acc | (x ^ y)} == 0
  }

  final case class InvalidHashFailure(msg: String) extends ValidationFailure {
    def failMsg = "Invalid hash: " + msg
  }

  final case class Base64DecodingSaltFailed(msg: String) extends ValidationFailure {
    def failMsg = "Base64 decoding of salt failed: " + msg
  }

  def main(args: Array[String]): Unit = {
    val password = ClearTextPassword[Validated]("123456")
    val password2 = ClearTextPassword[Validated]("123456")

    val hash: EncryptedPassword = createHash(password)
    println(hash)
    verifyPassword(password2, hash) match {
      case \/-(good) => println(s"matched $good")
      case -\/(err) => println(err); println("badddd")
    }
  }
}
