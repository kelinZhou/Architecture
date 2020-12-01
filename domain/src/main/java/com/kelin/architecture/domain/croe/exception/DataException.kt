package com.kelin.architecture.domain.croe.exception

class DataException(val errCode: Int, msg: String) : RuntimeException(msg)
