import kotlin.math.pow

var example_a = "123"
var example_b = "255"
var example_c = "253"
var example_d = "255"
var allowing_suffix = "29"
var CIDR_NOTATION_EXAMPLE = "$example_a.$example_b.$example_c.$example_d/$allowing_suffix"

var ALL_ADDRESSES = 2.0.pow(32)
var filterAddress: IPGroup? = null

data class IPGroup(val a: String,val b: String, val c: String, val d: String)

fun fromABCDToNumber(ip: IPGroup): Long {
    val (a, b, c, d) = ip
    return (a.toInt() * 256.0.pow(3) + b.toInt() * 256.0.pow(2) + c.toInt() * 256 + d.toInt()).toLong()
}

fun applyMask(base_address: Long, suffix: Int): Long{
    return base_address and (ALL_ADDRESSES - 2.0.pow(32-suffix)).toLong()
}

fun fromNumberToABCD(num: Long): IPGroup {
    var currNum = num
    val d = (currNum % 256).toString()
    currNum /= 256
    val c = (currNum % 256).toString()
    currNum /= 256
    val b = (currNum % 256).toString()
    currNum /= 256
    val a = (currNum % 256).toString()
    return IPGroup(a=a,b=b,c=c,d=d)
}

fun main() {
    val (prefix_address, suffix) = CIDR_NOTATION_EXAMPLE.split('/')
    println(prefix_address)
    println(suffix)
    val (a, b, c, d) = prefix_address.split(".")
    println("$a.$b.$c.$d")
    val ip = IPGroup(a=a,b=b,c=c,d=d)
    val ipAsNumber = fromABCDToNumber(ip)
    println(ipAsNumber)
    filterAddress = fromNumberToABCD(applyMask(base_address = ipAsNumber, suffix = suffix.toInt()))
    print(filterAddress)
    val checkForAddressValid = "123.255.253.255"
    println("The $prefix_address allowed: ${isAllowed(checkForAddressValid)}")
    val checkForAddressInvalid = "123.255.253.220"
    println("The $prefix_address allowed: ${isAllowed(checkForAddressInvalid)}")
}

fun isAllowed(incomingIp: String): Boolean {
    val (a, b, c, d) = incomingIp.split(".")
    val ipGroup = IPGroup(a = a, b = b, c = c, d = d)
    val incomingIpAsNum = fromABCDToNumber(ipGroup)
    // TODO - part of the class the suffix
    val incomingIPAfterMask = fromNumberToABCD(applyMask(incomingIpAsNum, allowing_suffix.toInt()))
    println("incomingIPAfterMask: $incomingIPAfterMask")
    return incomingIPAfterMask == filterAddress
}