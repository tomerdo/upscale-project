import kotlin.math.pow

private const val IPV4_BITS = 32
private const val SLASH_DELIMITER = '/'
private const val DOT_DELIMITER = "."

data class SubnetAddress(val a: String, val b: String, val c: String, val d: String)

class IPFilter(cidrNotation: String) {
    private val subnetAddress: SubnetAddress
    private val subnetMask: Long
    private val addresses = 2.0.pow(IPV4_BITS)
    private val classSize = 2.0.pow(IPV4_BITS / 4)

    init {
        val (prefixAddress, suffix) = cidrNotation.split(SLASH_DELIMITER)
        val (a, b, c, d) = prefixAddress.split(DOT_DELIMITER)
        val ipAsNumber = fromSubnetAddressToNumber(SubnetAddress(a = a, b = b, c = c, d = d))
        subnetMask = (addresses - 2.0.pow(IPV4_BITS - suffix.toInt())).toLong()
        subnetAddress = fromNumberToSubnetAddress(applyMask(ipAsNumber, subnetMask))

    }

    fun isAllowed(incomingIp: String): Boolean {
        val (a, b, c, d) = incomingIp.split(DOT_DELIMITER)
        val incomingSubnetAddress = SubnetAddress(a = a, b = b, c = c, d = d)
        val incomingIpAsNum = fromSubnetAddressToNumber(incomingSubnetAddress)
        val incomingSubnetAddressAfterMask = fromNumberToSubnetAddress(applyMask(incomingIpAsNum, subnetMask))
        return incomingSubnetAddressAfterMask == subnetAddress
    }

    private fun applyMask(inputAddress: Long, mask: Long): Long {
        return inputAddress and mask
    }

    private fun fromSubnetAddressToNumber(ip: SubnetAddress): Long {
        val (a, b, c, d) = ip
        return (a.toInt() * classSize.pow(3) + b.toInt() * classSize.pow(2) + c.toInt() * classSize + d.toInt()).toLong()
    }

    private fun fromNumberToSubnetAddress(num: Long): SubnetAddress {
        var currNum = num
        val classSizeAsInt = classSize.toInt()
        val d = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val c = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val b = (currNum % classSizeAsInt).toString()
        currNum /= classSizeAsInt
        val a = (currNum % classSizeAsInt).toString()
        return SubnetAddress(a = a, b = b, c = c, d = d)
    }
}

fun main() {
    val checkForAddressValid = "192.255.253.255"
    val checkForAddressInvalid = "192.255.253.220"

    val listOfCidrNotation = listOf("192.255.253.255/30", "192.255.253.255/28", "192.255.253.255/31")
    val anyForFirst = listOfCidrNotation.any { IPFilter(it).isAllowed(checkForAddressValid) }
    val anyForSecond = listOfCidrNotation.any { IPFilter(it).isAllowed(checkForAddressInvalid) }


    println("${listOfCidrNotation.joinToString(", ")} - Configured as the filters")
    println("$checkForAddressValid allowed: $anyForFirst")
    println("$checkForAddressInvalid allowed: $anyForSecond")
}

