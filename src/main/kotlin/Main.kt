import kotlin.math.pow

data class IPGroup(val a: String, val b: String, val c: String, val d: String)
const val IPV4_BITS = 32

private const val SLASH_DELIMITER = '/'
private const val DOT_DELIMITER = "."

class IPFilter(cidrNotation: String) {
    private val ipAllowedGroup: IPGroup
    private val groupMask: Long
    private val addresses = 2.0.pow(IPV4_BITS)
    private val classSize = 2.0.pow(IPV4_BITS / 4)

    init {
        val (prefixAddress, suffix) = cidrNotation.split(SLASH_DELIMITER)
        val (a, b, c, d) = prefixAddress.split(DOT_DELIMITER)
        val ipAsNumber = fromIPGroupToNumber(IPGroup(a = a, b = b, c = c, d = d))
        groupMask = (addresses - 2.0.pow(IPV4_BITS - suffix.toInt())).toLong()
        ipAllowedGroup = fromNumberToIPGroup(applyMask(ipAsNumber, groupMask))

    }

    fun isAllowed(incomingIp: String): Boolean {
        val (a, b, c, d) = incomingIp.split(DOT_DELIMITER)
        val ipGroup = IPGroup(a = a, b = b, c = c, d = d)
        val incomingIpAsNum = fromIPGroupToNumber(ipGroup)
        val incomingIPAfterMask = fromNumberToIPGroup(applyMask(incomingIpAsNum, groupMask))
        return incomingIPAfterMask == ipAllowedGroup
    }

    private fun applyMask(inputAddress: Long, mask: Long): Long {
        return inputAddress and mask
    }

    private fun fromIPGroupToNumber(ip: IPGroup): Long {
        val (a, b, c, d) = ip
        return (a.toInt() * classSize.pow(3) + b.toInt() * classSize.pow(2) + c.toInt() * classSize + d.toInt()).toLong()
    }

    private fun fromNumberToIPGroup(num: Long): IPGroup {
        var currNum = num
        val classSizeAsInt = classSize.toInt()
        val d = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val c = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val b = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val a = (currNum % classSizeAsInt).toString()
        return IPGroup(a = a, b = b, c = c, d = d)
    }
}

fun main() {
    val cidrNotation = "192.255.253.255/30"
    val ipFilter = IPFilter(cidrNotation)
    val checkForAddressValid = "192.255.253.255"
    val checkForAddressInvalid = "192.255.253.220"

    println("$cidrNotation - Configured as the filter")
    println("$checkForAddressValid allowed: ${ipFilter.isAllowed(checkForAddressValid)}")
    println("$checkForAddressInvalid allowed: ${ipFilter.isAllowed(checkForAddressInvalid)}")
}

