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

CREATE OR REPLACE FUNCTION informacoes_totais_globais()
RETURNS TABLE (
				codigo int,
				pais varchar(100),
				casos int,
				mortes int,
				testes int,
				vacinacoes int,
				hospitalizacoes int
			   ) AS $$
			   
	BEGIN
		RETURN QUERY
			SELECT  informacoes_totais_globais.paisid, informacoes_totais_globais.nome, 
					MAX(total_casos) as total_casos, 
					MAX(total_mortes) as total_mortes, 
					MAX(total_testes) as total_testes, 
					MAX(total_vacinacoes) as total_vacinacoes, 
					MAX(total_hospitalizacoes) as total_hospitalizacoes
			FROM (
				SELECT * FROM 
					(SELECT informacoes_globais_casos.paisid, datas, quantidade_total AS total_casos 
					 FROM informacoes_globais_casos) 
						AS casos
					NATURAL JOIN 
					(SELECT informacoes_globais_mortes.paisid, datas, quantidade_total AS total_mortes 
					 FROM informacoes_globais_mortes)
						AS mortes
					NATURAL JOIN
					(SELECT informacoes_globais_testes.paisid, datas, quantidade_total AS total_testes 
					 FROM informacoes_globais_testes)
						AS testes
					NATURAL JOIN
					(SELECT informacoes_globais_vacinacoes.paisid, datas, quantidade_total AS total_vacinacoes 
					 FROM informacoes_globais_vacinacoes)
						AS vacinacoes
					NATURAL JOIN
					(SELECT informacoes_globais_hospitalizacoes.paisid, datas, quantidade_total AS total_hospitalizacoes 
					 FROM informacoes_globais_hospitalizacoes)
						AS hospitalizacoes
					INNER JOIN
					ente_federativo
						ON ente_federativo.codigo = casos.paisid
			) AS informacoes_totais_globais
			GROUP BY paisid, nome;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION total_registros_estados()
RETURNS TABLE (
				UF varchar(2),
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
			with registros_estado as (
				select estado.sigla, registro.dataregistro, registro.clinica_ocup_suspeita, registro.clinica_ocup_confirmado,
					   registro.uti_ocup_suspeita, registro.uti_ocup_confirmado, registro.obitos_suspeita, registro.obitos_confirmado,
					   registro.alta_suspeita, registro.alta_confirmado
					from 
						estado inner join endereco
							on estado.codigo = endereco.estado
						natural join estabelecimento_de_saude
						inner join registro_de_ocupacao 
							on estabelecimento_de_saude.cnes = registro_de_ocupacao.cnes
						inner join registro
							on registro.codigo = registro_de_ocupacao.codigo
			)

			select  registros_estado.sigla,
					sum(registros_estado.clinica_ocup_suspeita)::int 
						as total_clinica_ocup_suspeita,
					sum(registros_estado.clinica_ocup_confirmado)::int 
						as total_clinica_ocup_confirmado,
					sum(registros_estado.uti_ocup_suspeita)::int 
						as total_uti_ocup_suspeita,
					sum(registros_estado.uti_ocup_confirmado)::int 
						as total_uti_ocup_confirmado,
					sum(registros_estado.obitos_suspeita)::int 
						as total_obitos_suspeita,
					sum(registros_estado.obitos_confirmado)::int 
						as total_obitos_confirmado,
					sum(registros_estado.alta_suspeita)::int 
						as total_alta_suspeita,
					sum(registros_estado.alta_confirmado)::int 
						as total_alta_confirmado
			from registros_estado
			group by sigla;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION total_registros_municipios(UF_input char(2))
RETURNS TABLE (
				nome varchar(100),
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
			WITH registros_municipio AS (
				SELECT  ente_federativo.nome, 
						registro.dataregistro, registro.clinica_ocup_suspeita,
						registro.clinica_ocup_confirmado,
						registro.uti_ocup_suspeita, registro.uti_ocup_confirmado,
						registro.obitos_suspeita, registro.obitos_confirmado,
						registro.alta_suspeita, registro.alta_confirmado
					FROM estado
					INNER JOIN municipio
						ON municipio.estadoid = estado.codigo
					INNER JOIN ente_federativo
						ON municipio.codigo = ente_federativo.codigo
					INNER JOIN endereco
						ON municipio.codigo = endereco.municipio
					INNER JOIN estabelecimento_de_saude
						ON endereco.cep = estabelecimento_de_saude.cep
					INNER JOIN registro_de_ocupacao
						ON estabelecimento_de_saude.cnes = registro_de_ocupacao.cnes
					INNER JOIN registro
						ON registro_de_ocupacao.codigo = registro.codigo
					WHERE estado.sigla = UF_input
			)
			
			SELECT registros_municipio.nome,
					sum(registros_municipio.clinica_ocup_suspeita)::int 
						as total_clinica_ocup_suspeita,
					sum(registros_municipio.clinica_ocup_confirmado)::int 
						as total_clinica_ocup_confirmado,
					sum(registros_municipio.uti_ocup_suspeita)::int 
						as total_uti_ocup_suspeita,
					sum(registros_municipio.uti_ocup_confirmado)::int 
						as total_uti_ocup_confirmado,
					sum(registros_municipio.obitos_suspeita)::int 
						as total_obitos_suspeita,
					sum(registros_municipio.obitos_confirmado)::int 
						as total_obitos_confirmado,
					sum(registros_municipio.alta_suspeita)::int 
						as total_alta_suspeita,
					sum(registros_municipio.alta_confirmado)::int 
						as total_alta_confirmado
			from registros_municipio
			group by registros_municipio.nome;
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION media_movel_casos_pais(nome_pais varchar(100))
RETURNS TABLE (
				datas date,
				novos int,
				media_movel decimal
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_casos.datas, informacoes_globais_casos.novos,
				   AVG(informacoes_globais_casos.novos) OVER(ORDER BY informacoes_globais_casos.datas 
				   										ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_casos
					ON codigo_pais.codigo = informacoes_globais_casos.paisid;
					
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION media_movel_mortes_pais(nome_pais varchar(100))
RETURNS TABLE (
				datas date,
				novos int,
				media_movel decimal
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT informacoes_globais_mortes.datas, informacoes_globais_mortes.novos,
				   AVG(informacoes_globais_mortes.novos) OVER(ORDER BY informacoes_globais_mortes.datas 
				   										ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
			FROM 
				(SELECT codigo FROM paises_info WHERE nome = nome_pais) AS codigo_pais
				INNER JOIN informacoes_globais_mortes
					ON codigo_pais.codigo = informacoes_globais_mortes.paisid;
				
	END
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION media_movel_paises()
RETURNS TABLE (
				nomes varchar(30),
				datas date,
				novos_casos int,
				media_movel_casos decimal,
				novas_mortes int,
				media_movel_mortes decimal
			   ) AS $$
	BEGIN
		RETURN QUERY
			WITH paises_info AS (
				SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo
			)

			SELECT codigo_pais.nome, informacoes_globais_casos.datas, 
				   informacoes_globais_casos.novos,
				   AVG(informacoes_globais_casos.novos) OVER(PARTITION BY codigo_pais.nome 
				   										ORDER BY informacoes_globais_casos.datas 
				   										ROWS BETWEEN 6 PRECEDING AND CURRENT ROW),
				   informacoes_globais_mortes.novos,
				   AVG(informacoes_globais_mortes.novos) OVER(PARTITION BY codigo_pais.nome 
				   										 ORDER BY informacoes_globais_mortes.datas 
				   										 ROWS BETWEEN 6 PRECEDING AND CURRENT ROW)
			FROM 
				(SELECT codigo, nome FROM paises_info) AS codigo_pais
				INNER JOIN informacoes_globais_mortes
				ON codigo_pais.codigo = informacoes_globais_mortes.paisid
				INNER JOIN informacoes_globais_casos
				ON informacoes_globais_mortes.paisid = informacoes_globais_casos.paisid
			
			WHERE informacoes_globais_mortes.datas = informacoes_globais_casos.datas;
				
	END
$$ LANGUAGE plpgsql;
			 
-- SELECT * FROM registros_por_estado('Minas Gerais');
-- SELECT * FROM municipios_por_estado('AC');
-- SELECT * FROM registros_por_municipio('Acrelândia');
-- SELECT * FROM informacoes_casos_por_pais('Brazil');
-- SELECT * FROM informacoes_mortes_por_pais('Brazil');
-- SELECT * FROM informacoes_testes_por_pais('Brazil');
-- SELECT * FROM informacoes_vacinacao_por_pais('Brazil');
-- SELECT * FROM registros_por_estado('SP');
-- SELECT * FROM media_movel_casos_pais('Brazil');
SELECT * FROM media_movel_mortes_pais('Brazil');
-- select * from registros_por_municipio('São Paulo');

-- select * from total_registros_estados();
-- SELECT * FROM total_registros_municipios('SP');
-- SELECT * FROM informacoes_totais_globais();








