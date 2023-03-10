package eden.historical.fetching

import eden.historical.Settings

private data class LoginTokens(val csrfToken: String, val n: String)

class GoodreadsFetcher :  RequiresLoginFetcher<JsoupFetcher>(JsoupFetcher()) {
    override fun doLogin() {
        println("Not logged in to GoodReads, logging in now.")
        val tokens = getLoginTokens()
        val loginData = makeloginData(tokens)
        val document = nestedFetcher.post(Settings.goodreadsLoginUrl, data = loginData)
        println("Logged in to GoodReads")
    }

    private fun makeloginData(data: LoginTokens) : Map<String, String> = mapOf(
            "appActionToken" to "kZRkEQN8BBwayTnyvyyj2BeUaSbAAj3D",
            "appAction" to "SIGNIN",
            "siteState" to "ape:OTkzZDgzOGRkMjFhMjk0ODIyNGVkMmU4M2IzOTk0N2Y=",
            "openid.return_to" to "ape:aHR0cHM6Ly93d3cuZ29vZHJlYWRzLmNvbS9hcC1oYW5kbGVyL3NpZ24taW4=",
            "prevRID" to "ape:R0gwTlNKUUVaS01BWU41N1BLRUI=",
            "workflowState" to "eyJ6aXAiOiJERUYiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiQTI1NktXIn0.hSWZuvzcF_tJscen5SE7YwTza8GHTbjpSEXi_NCRlof5j6fCLPGBQA._LwsW7D606AZUSnj.r0jJVmz8A-aqiNOS1R_vAeNVUIew4oCa1DGNNbjQmTgWlObStF8iUjeG-FTBYbkx7B4RlwAynAoVAT64d7fpOdXE4nNvlLlovmTRxmT9YhjYvG3woM0-OkBD2hZUAJnKs9yCvgjxU6eY_wEnmd45jXfap1zL6_rHGWjCgikXCCvvpn_1LtXwduobSu9O6xmUSX-WymAimimk-RK7NhlPiwJXUuDJHMabxrqi-Ro6hsyd_biWpDsl2yOObkbsZvS6uIwUJ4IgbCk3DxiKap5jMjVQTAN1ZOXFzRxZzda5lBv-vcfoFr2tGiE_FOv_IV-DXwsq0BXwCVoshAGrgJ3AebdM5oTRBPfFsfKInm4tlHViYFqa1Ai-Cawf8Ytb53laTFwTP3Gdbw.KK9GIpDoIpU599vOBx-exg",
            "email" to "martin.s.eden@protonmail.ch",
            "create" to "0",
            "password" to "hrfxrbboLASMY4aoHvX2",
            "encryptedPasswordExpected" to "",
            "metadata1" to "ECdITeCs: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"
    )

    private fun getLoginTokens() : LoginTokens {
        println("Retrieving CSRF token from login page")
        val doc = nestedFetcher.get(Settings.goodreadsLoginUrl)

        print(doc.html())
        val formInputs = doc.select("div#emailForm form input")
        print(formInputs.html())
        val csrfToken = formInputs
            .first { it.attr("name") == "authenticity_token" }
            .attr("value")
        val n = formInputs
            .first { it.attr("name") == "n" }
            .attr("value")
        return LoginTokens(csrfToken, n)
    }

}