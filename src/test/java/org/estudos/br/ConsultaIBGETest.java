package org.estudos.br;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConsultaIBGETest {
    private static final String ESTADOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/estados/";
    private static final String DISTRITOS_API_URL = "https://servicodados.ibge.gov.br/api/v1/localidades/distritos/";

    @Test
    @DisplayName("Teste para consulta única de um estado")
    public void testConsultarEstado() throws IOException {
        // Arrange
        String uf = "SP"; // Define o estado a ser consultado

        // Act
        String resposta = ConsultaIBGE.consultarEstado(uf); // Chama o método a ser testado

        // Assert
        // Verifica se a resposta não está vazia
        assert !resposta.isEmpty();

        // Verifica se o status code é 200 (OK)
        HttpURLConnection connection = (HttpURLConnection) new URL(ESTADOS_API_URL + uf).openConnection();
        int statusCode = connection.getResponseCode();
        assertEquals(200, statusCode, "O status code da resposta da API deve ser 200 (OK)");
    }


    @Test
    @DisplayName("Testa resposta vazia para estado inexistente")
    public void testRespostaVaziaEstado() throws IOException {
        // Arrange
        String uf = "ZZ"; // Estado que não existe

        // Act
        String resultado = ConsultaIBGE.consultarEstado(uf);

        // Assert
        // Verifica se o resultado de uma sigla inexistente é uma string vazia ou um array vazio
        assertTrue(resultado.isEmpty() || resultado.equals("[]"),
                "A resposta deve ser vazia");
    }


    @Test
    @DisplayName("Teste para consulta única de um distrito")
    public void testconsultarDistrito() throws IOException {
        // Arrange
        int distritoId = 520005005; // Id do distrito

        // Act
        String resposta = ConsultaIBGE.consultarDistrito(distritoId); // Chama o método a ser testado

        // Assert
        // Verifica se a resposta não está vazia
        assert !resposta.isEmpty();

        System.out.println(resposta);

        // Verifica se a resposta contém as chaves "id" e "nome"
        assertTrue(resposta.contains("\"id\":") && resposta.contains("\"nome\":"),
                "A resposta deve conter a chave 'id' e 'nome'");
    }


    @Test
    @DisplayName("Teste com Mockito para consulta de um distrito com resposta mockada")
    public void testConsultarDistritoComMock() throws IOException {
        // Arrange
        int distritoId = 520005005; // Id do distrito
        String respostaMockada = "{\"id\":520005005,\"nome\":\"Abadia de Goiás\",\"municipio\":{\"id\":5200050,\"nome\":\"Abadia de Goiás\",\"microrregiao\":{\"id\":52010,\"nome\":\"Goiânia\",\"mesorregiao\":{\"id\":5203,\"nome\":\"Centro Goiano\",\"UF\":{\"id\":52,\"sigla\":\"GO\",\"nome\":\"Goiás\",\"regiao\":{\"id\":5,\"sigla\":\"CO\",\"nome\":\"Centro-Oeste\"}}}},\"regiao-imediata\":{\"id\":520001,\"nome\":\"Goiânia\",\"regiao-intermediaria\":{\"id\":5201,\"nome\":\"Goiânia\",\"UF\":{\"id\":52,\"sigla\":\"GO\",\"nome\":\"Goiás\",\"regiao\":{\"id\":5,\"sigla\":\"CO\",\"nome\":\"Centro-Oeste\"}}}}}}";

        InputStream mockInputStream = new ByteArrayInputStream(respostaMockada.getBytes());
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        when(mockConnection.getInputStream()).thenReturn(mockInputStream);

        URL mockUrl = mock(URL.class);
        when(mockUrl.openConnection()).thenReturn(mockConnection);

        ConsultaIBGE consulta = new ConsultaIBGE() {
            protected URL createUrl(String path) throws IOException {
                return mockUrl;
            }
        };

        // Act
        String resposta = consulta.consultarDistrito(distritoId);

        // Assert
        assertFalse(resposta.isEmpty(), "A resposta não deve ser vazia.");
        assertTrue(resposta.contains("\"nome\":\"Abadia de Goiás\""), "A resposta deve conter o nome do distrito.");
    }


}