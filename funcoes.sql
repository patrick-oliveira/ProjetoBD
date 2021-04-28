CREATE OR REPLACE FUNCTION registros_por_estado(nome_input varchar(100))
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
				SELECT cnes from
					(SELECT * FROM
						(SELECT ente_federativo.codigo FROM ente_federativo WHERE nome = nome_input) AS EF NATURAL JOIN estado) as UF
					INNER JOIN 
					endereco 
						ON UF.codigo = estado
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

			 
SELECT * FROM registros_por_estado('Minas Gerais');


-- ;