@Test
fun login_con_usuario_valido_devuelve_rol() = runTest {
    repo.insertarUsuario("Admin", "admin@cadiz.es", "admin1234", RolUsuario.ADMIN)

    val result = viewModel.login("admin@cadiz.es", "admin1234")

    assertEquals(RolUsuario.ADMIN, result.getOrNull())
}