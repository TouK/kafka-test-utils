package pl.touk.utils.kafkatest

import java.net.ServerSocket

object AvailablePortFinder {
  type Port = Int

  def findAvailablePorts(n: Int): List[Port] = {
    val sockets: List[(Port, ServerSocket)] =
      (0 until n).map { _ =>
        val socket = new ServerSocket(0)
        socket.setReuseAddress(true)
        (socket.getLocalPort, socket)
      }.toList

    try
      sockets.map(_._1)
    finally
      sockets.foreach(_._2.close())
  }
}
