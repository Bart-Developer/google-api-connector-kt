package me.bartolome.library.service.impl

import me.bartolome.library.CreateXlsx
import me.bartolome.library.model.Person
import org.junit.Test

class CreateXlsxTest{

    @Test
    fun `Create a Xlsx File from a Person Data`() {

        val person = Person(200, "123", type = "F", customer = true, false)

        val status = CreateXlsx.createGoogleDriveDocument(person)

        assert(status.statusCode.toString().contains("200 OK"))
    }
}