package me.bartolome.library.model

class Person(
    val idPomPerson: Long?,
    val idHost: String,
    val type: String?,
    val customer: Boolean?,
    val employee: Boolean?
) {

    override fun toString(): String {
        return "Person {" +
                "id_pom_person='" + idPomPerson + '\'' +
                ", id_host='" + idHost + '\'' +
                ", person_type='" + type + '\'' +
                ", is_customer='" + customer + '\'' +
                ", is_employee='" + employee + '\'' +
                '}'
    }
}