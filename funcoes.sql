CREATE OR REPLACE FUNCTION registros_por_estado(sigla_input char(2))
RETURNS TABLE (
				dataregistro date,
				clinica_ocup_suspeita int,
				clinica_ocup_confirmado int,
				uti_ocup_suspeita int,
				uti_ocup_confirmado int,
				obitos_suspeita int,
				obitos_confirmado int,
				alta_suspeita int,
				alta_confirmado int
			  ) AS $$
	BEGIN
		RETURN QUERY
			WITH CNES_UF AS (
				SELECT cnes FROM
					(SELECT codigo FROM estado WHERE estado.sigla = sigla_input) AS UF_CODIGO
					INNER JOIN 
					endereco 
						ON UF_CODIGO.codigo = endereco.estado
					NATURAL JOIN
					estabelecimento_de_saude
			)

			SELECT registro.dataregistro,
				   registro.clinica_ocup_suspeita, registro.clinica_ocup_confirmado,
				   registro.uti_ocup_suspeita, registro.uti_ocup_confirmado,
				   registro.obitos_suspeita, registro.obitos_confirmado,
				   registro.alta_suspeita, registro.alta_confirmado 
			FROM registro NATURAL JOIN (registro_de_ocupacao NATURAL JOIN CNES_UF) as REGISTROS_UF;
	END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION registros_por_municipio(nome_input varchar(100))
RETURNS TABLE (
				dataregistro date,
				clinica_ocup_suspeita int,
				clinica_ocup_confirmado int,
				uti_ocup_suspeita int,
				uti_ocup_confirmado int,
				obitos_suspeita int,
				obitos_confirmado int,
				alta_suspeita int,
				alta_confirmado int
			  ) AS $$
	BEGIN
		RETURN QUERY
			WITH cnes_municipio AS (
				SELECT cnes FROM
					(SELECT * FROM
						(SELECT ente_federativo.codigo FROM ente_federativo WHERE nome = nome_input) AS EF NATURAL JOIN municipio) AS codigo_ibge
					INNER JOIN endereco
						ON codigo_ibge.codigo = municipio
					NATURAL JOIN
						estabelecimento_de_saude
			)

			SELECT registro.dataregistro,
				   registro.clinica_ocup_suspeita, registro.clinica_ocup_confirmado,
				   registro.uti_ocup_suspeita, registro.uti_ocup_confirmado,
				   registro.obitos_suspeita, registro.obitos_confirmado,
				   registro.alta_suspeita, registro.alta_confirmado 
			FROM registro NATURAL JOIN (registro_de_ocupacao NATURAL JOIN cnes_municipio) as REGISTROS_UF;
	END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION informacoes_casos_por_pais(nome_pais varchar(30))
RETURNS TABLE (
				datas date,
				novos int,
				quantidade_total int,
				novos_por_milhao float,
				total_por_milhao float
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_casos.datas, informacoes_globais_casos.novos,
				   informacoes_globais_casos.quantidade_total, informacoes_globais_casos.novos_por_milhao,
				   informacoes_globais_casos.total_por_milhao
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_casos
					ON codigo_pais.codigo = informacoes_globais_casos.paisid;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION informacoes_mortes_por_pais(nome_pais varchar(30))
RETURNS TABLE (
				datas date,
				novos int,
				quantidade_total int,
				novos_por_milhao float,
				total_por_milhao float
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_mortes.datas, informacoes_globais_mortes.novos,
				   informacoes_globais_mortes.quantidade_total, informacoes_globais_mortes.novos_por_milhao,
				   informacoes_globais_mortes.total_por_milhao
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_mortes
					ON codigo_pais.codigo = informacoes_globais_mortes.paisid;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION informacoes_testes_por_pais(nome_pais varchar(30))
RETURNS TABLE (
				datas date,
				novos int,
				quantidade_total int,
				novos_por_milhar float,
				total_por_milhar float,
				taxa_positivos float,
				testes_por_casos float
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_testes.datas, informacoes_globais_testes.novos,
				   informacoes_globais_testes.quantidade_total, informacoes_globais_testes.novos_por_milhar,
				   informacoes_globais_testes.total_por_milhar, informacoes_globais_testes.taxa_positivos,
				   informacoes_globais_testes.testes_por_casos
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_testes
					ON codigo_pais.codigo = informacoes_globais_testes.paisid;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION informacoes_vacinacao_por_pais(nome_pais varchar(30))
RETURNS TABLE (
				datas date,
				novos int,
				quantidade_total int,
				total_por_centena float
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_vacinacoes.datas, informacoes_globais_vacinacoes.novos,
				   informacoes_globais_vacinacoes.quantidade_total, informacoes_globais_vacinacoes.total_por_centena
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_vacinacoes
					ON codigo_pais.codigo = informacoes_globais_vacinacoes.paisid;
	END
$$ LANGUAGE plpgsql;



			   

CREATE OR REPLACE FUNCTION municipios_por_estado(uf_sigla char(2))
RETURNS TABLE (
				codigo int,
				nome varchar(100)
			  ) AS $$
	BEGIN
		RETURN QUERY
			WITH municipios_UF AS
				(SELECT municipio.codigo
				FROM 
					(SELECT * FROM estado WHERE sigla = uf_sigla) as UF 
					INNER JOIN municipio 
						ON municipio.estadoid = UF.codigo)

			SELECT municipios_UF.codigo, ente_federativo.nome
			FROM municipios_UF INNER JOIN ente_federativo
				ON municipios_UF.codigo = ente_federativo.codigo;
	END;
$$ LANGUAGE plpgsql;

			 
-- SELECT * FROM registros_por_estado('Minas Gerais');
-- SELECT * FROM municipios_por_estado('AC');
-- SELECT * FROM registros_por_municipio('Acrelândia');
-- SELECT * FROM informacoes_casos_por_pais('Brazil');
-- SELECT * FROM informacoes_mortes_por_pais('Brazil');
-- SELECT * FROM informacoes_testes_por_pais('Brazil');
-- SELECT * FROM informacoes_vacinacao_por_pais('Brazil');

WITH informacoes_casos_pais AS(
	SELECT * FROM informacoes_casos_por_pais('Brazil')
)

SELECT datas,
	AVG(novos) OVER(ORDER BY datas
		 		    ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
	AS movin_average
FROM informacoes_casos_pais;
