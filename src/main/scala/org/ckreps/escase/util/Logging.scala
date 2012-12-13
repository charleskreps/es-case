package org.ckreps.escase.util

import org.slf4j.LoggerFactory

/**
 * @author ckreps
 */
trait Logging {

  private val logger = LoggerFactory.getLogger(getClass)

  def info(msg:Any*) = logger.info(msg.mkString("\n"))
}
