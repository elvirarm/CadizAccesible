@Test
fun calcula_total_urgentes_correctamente() = runTest {
    repo.insertarIncidencia(incidenciaUrgente)
    repo.insertarIncidencia(incidenciaNormal)

    viewModel.cargar()

    assertEquals(1, viewModel.totalUrgentes.value)
}