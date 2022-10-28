package me.bartolome.library

import me.bartolome.library.model.Person
import me.bartolome.library.utils.connectionFactoryWithApiGoogle
import com.google.api.client.http.ByteArrayContent
import com.google.api.services.drive.model.File
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class CreateXlsx {
    companion object {

        private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

        fun createGoogleDriveDocument(person: Person): ResponseEntity<String> {

            val DIRECTORY_DRIVE_ID = "1lGHeTNdkklmUfouFrz-7xo4VICi-lpbN"

            val filename =
                "Persona sin trivial ${TimeReport.getTheTimeReport().formattedDate} at ${TimeReport.getTheTimeReport().hourFormatted} Hs.xlsx"

            val report = generateXlsxFile(person)

            val content =
                ByteArrayContent("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                    report.toByteArray())

            val fileMetadata = File()
                .setName(filename)
                .setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .setParents(listOf(DIRECTORY_DRIVE_ID))

            val newFileXlsx = connectionFactoryWithApiGoogle()!!.files()
                .create(fileMetadata, content)
                .setFields("id").execute()

            logger.info("Report $filename Create & uploaded file successfully with ID ${newFileXlsx.id}")

            val responseHeaders = HttpHeaders()
            responseHeaders.set(newFileXlsx.id.toString(), "Header")

            return ResponseEntity.ok()
                .body(newFileXlsx.id);
        }

        data class TimeReport(
            val formattedDate: String?,
            val hourFormatted: String,
        ) {
            companion object {
                fun getTheTimeReport(): TimeReport {
                    val date = LocalDateTime.now()
                    val formattedDate: String = date.format(DateTimeFormatter
                        .ofLocalizedDate(FormatStyle.LONG))
                    val hourFormatted: String = date.format(DateTimeFormatter
                        .ofPattern("h:mm a"))

                    return TimeReport(
                        formattedDate = formattedDate,
                        hourFormatted = hourFormatted
                    )
                }
            }
        }

        private fun generateXlsxFile(person: Person): ByteArrayOutputStream {
            val stream = ByteArrayOutputStream()
            val unregisteredClients = mapForWrite(person)
            val workbook = writeNonRegisteredClientsToWorkbook(unregisteredClients)
            workbook?.write(stream)
            workbook?.close()
            return stream
        }

        private fun mapForWrite(person: Person): List<Person> {

            val clients = mutableMapOf<String, Person>()

            val unregisteredClient = Person(
                person.idPomPerson,
                person.idHost,
                person.type,
                person.customer,
                person.employee,
            )
            clients[unregisteredClient.idHost] = unregisteredClient
            return clients.values.toList()
        }

        private fun writeNonRegisteredClientsToWorkbook(clients: List<Person>): XSSFWorkbook {

            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet()

            writeHeaders(sheet)

            var rowIndex = 1
            clients.forEach { _client ->
                val row = sheet.createRow(rowIndex)
                writeClientToRow(row, _client)
                rowIndex++
            }
            return workbook
        }

        private fun writeClientToRow(row: Row, client: Person) {
            var cellIndex = 0
            writeCell(row, cellIndex++, client.idPomPerson.toString())
            writeCell(row, cellIndex++, client.idHost)
            writeCell(row, cellIndex++, client.type!!)
            writeCell(row, cellIndex++, client.customer.toString())
            writeCell(row, cellIndex, client.employee.toString()
            )
        }

        private fun writeCell(row: Row, cellIndex: Int, value: String) {
            val cell = row.createCell(cellIndex, CellType.STRING)
            cell.setCellValue(value)
        }

        private fun writeHeaders(sheet: XSSFSheet) {
            val row = sheet.createRow(0)
            val headers = listOf("id_pom_person", "id_host", "person_type", "is_customer", "is_employee")
            var cellIndex = 0
            headers.forEach { _header ->
                val cell = row.createCell(cellIndex, CellType.STRING)
                cell.setCellValue(_header)
                cellIndex++
            }
        }
    }
}