-- � poss�vel criar as tabelas na ordem em que elas aparecem.

create table ente_federativo(
	ID int,
	nome varchar(100) not null,
	populacao int not null,
	densidade float,
	primary key(ID)
);

-- Obs: foi necessario adicionar o constraint para poder fazer refer�ncia ao ID da especizaliza��o
-- a partir de outras tabelas. Isso quando uma chave estrangeira aponta para outra chave estrangeira...
create table estado(
	ID int,
	constraint estadoID unique (ID),
	foreign key (ID) references ente_federativo(ID)
);

create table municipio(
	ID int,
	estadoID int,
	constraint municipioID unique (ID),
	foreign key (ID) references ente_federativo(ID),
	foreign key (estadoID) references estado(ID)
);

create table pais(
	ID int,
	constraint paisID unique (ID),
	foreign key (ID) references ente_federativo(ID)
);

create table informacoes_globais_mortes(
	paisID int,
	datas date not null, -- escrito "datas" pois "data" � palavra reservada.
	novos int,
	quantidade_total int,
	novos_por_milhao float,
	total_por_milhao float,
	foreign key (paisID) references pais(ID)
);

create table informacoes_globais_casos(
	paisID int,
	datas date not null,
	novos int,
	quantidade_total int,
	novos_por_milhao float,
	total_por_milhao float,
	foreign key (paisID) references pais(ID)
);

create table informacoes_globais_testes(
	paisID int,
	datas date not null,
	novos int,
	novos_por_milhar float,
	quantidade_total int,
	total_por_milhar float,
	taxa_positivos float,
	testes_por_casos float,
	unidades int,
	foreign key (paisID) references pais(ID)
);

create table informacoes_globais_vacinacoes(
	paisID int,
	datas date not null,
	novos int,
	quantidade_total int,
	total_por_centena float,
	pessoas_total_vac int,
	pessoas_total_vac_por_centena float,
	foreign key (paisID) references pais(ID)
);

create table informacoes_globais_hospitalizacoes(
	paisID int,
	datas date not null,
	novos int,
	quantidade_total int,
	pacientes_uti int,
	pacientes_uti_por_milhao float,
	admissoes_semanais_uti float,
	admissoes_semanais float,
	foreign key (paisID) references pais(ID)
);

create table relatorio_covid(
	ente_federativo int,
	datas date,
	mortes int,
	casos_confirmados int,
	pedidos_por_leito float,
	casos_confirmados_100k float,
	taxa_mortalidade float,
	foreign key (ente_federativo) references ente_federativo(ID),
	primary key (datas)
);

create table registro_acumulado_cartorio(
	estado int, -- considerei uma chave externa para o ID, por isso int...
	datas date,
	total int,
	indeterminado int,
	septicemia int,
	sars int,
	falha_respirador int,
	pneumonia int,
	foreign key (estado) references estado(ID),
	primary key (datas)
);

create table registro_diario_cartorio(
	estado int,
	datas date,
	total int,
	indeterminado int,
	septicemia int,
	sars int,
	falha_respirador int,
	pneumonia int,
	foreign key (estado) references estado(ID),
	primary key (datas)
);

create table item(
	itemID int,
	fornecedor varchar(50),
	descricao varchar(200),
	nome varchar(50),
	tipo varchar(50),
	vinculo varchar(50), --- tipo de atributo ???
	primary key (itemID)
);

create table registro_entrega(
	entregaCodigo int,
	itemID int,
	status varchar(30),
	destinatario varchar(200),
	valor_total float,
	quantidade float, -- float, l�quido pode ser em litros?
	data_entrega date,
	primary key (entregaCodigo),
	foreign key (itemID) references item(itemID)
);

create table entregas_realizadas(
	codigo int,
	enteID int,
	foreign key (codigo) references registro_entrega(entregaCodigo),
	foreign key (enteID) references ente_federativo(ID)
);

create table registro (
	codigo int, -- ou � string? (ex p5Ez41Zu6B).
	dataRegistro date,
	clinica_ocup_suspeita int,
	clinica_ocup_confirmado int,
	uti_ocup_suspeita int,
	uti_ocup_confirmado int,
	obitos_suspeita int,
	obitos_confirmado int,
	alta_suspeita int,
	alta_confirmado int,
	primary key (codigo)
);

create table estabelecimento_de_saude(
	CNES char(7), -- Inicialmente feito com int, mas depois pensei nos 0s a esquerda.
	nome varchar(200) not null,
	cep varchar(8), -- xxxxx-xxx ou xxxxxxxx
	primary key (CNES)
);

create table registro_de_ocupacao(
	CNES char(7),
	codigo int,
	foreign key (CNES) references estabelecimento_de_saude(CNES),
	foreign key (codigo) references registro(codigo)
);

create table pessoa(
	CPF varchar(14), -- no maximo xxx.xxx.xxx-xx
	nome varchar(100) not null,
	sexo varchar(10),
	nascimento date,
	idade int,
	raca_cor varchar(10),
	escolaridade varchar(100),
	CEP varchar(9),
	municipioId int,
	estado int, --- int pensando no ID... fiquei em duvida
	rua varchar(100),
	bairro varchar(100),
	numero smallint,
	complemento text,
	telefone varchar(20), --- varios formatos? possivelmente bigint(11). ex: 11234567899
	tipo_idade varchar(30), --- x para ano, y para mes, z para dia?
	ocupa��o varchar(30),
	zona varchar(10),
	primary key (CPF),
	foreign key (municipioId) references municipio(ID)
);

create table paciente(
	codigo_paciente int,
	gestante varchar(30),
	evol_sg_srag varchar(20),
	nosocomial varchar(20),
	contato_su�no_ave varchar(20),
	contato_outro_animal varchar(60),
	data_vacina_gripe date,
	data_vacina_m�e date,
	amamenta��o varchar(20),
	data_dose_unica date,
	data_primeira_dose date,
	data_segunda_dose date,
	criterio_encerramento varchar(30),
	evolu��o_caso varchar(30),
	data_alta_obito date,
	data_encerramento date,
	primary key (codigo_paciente)
);

create table registro_pacientes(
	CPF varchar(14),
	codigo_paciente int,
	foreign key (CPF) references pessoa(CPF),
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table viagem_internacional(
	codigo_paciente int,
	pais varchar(100),
	lugar varchar(100),
	data_ida date,
	data_volta date,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table sintomas(
	codigo_paciente int,
	nome varchar(100),
	descricao text,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table fator_de_risco(
	codigo_paciente int,
	nome varchar(100), 
	descricao text,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table anti_viral(
	codigo_paciente int,
	nome varchar(100),
	data_inicial date,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table raiox(
	codigo_paciente int,
	datas date,
	resultado text,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table tomografia(
	codigo_paciente int,
	datas date,
	resultado text,
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table coleta(
	codigo_paciente int,
	codigoColeta int,
	datas date,
	tipo varchar(30),
	primary key (codigoColeta),
	foreign key (codigo_paciente) references paciente(codigo_paciente)
);

create table teste(
	codigo int,
	data_resultado date,
	primary key (codigo)
);

create table teste_coleta(
	codigo_coleta int,
	codigo_teste int,
	foreign key (codigo_coleta) references coleta(codigoColeta),
	foreign key (codigo_teste) references teste(codigo)
);

create table internacao(
	codigo_internacao int,
	data_entrada date,
	data_entrada_uti date,
	data_saida_UTI date,
	uso_suporte_respiratorio varchar(30),
	primary key (codigo_internacao)
);

create table pacientes_internados(
	codigo_paciente int,
	codigo_internacao int,
	foreign key (codigo_paciente) references paciente(codigo_paciente),
	foreign key (codigo_internacao) references internacao(codigo_internacao)
);

create table internacoes_locais(
	codigo_internacao_paciente int,
	CNES char(7),
	foreign key (codigo_internacao_paciente) references internacao(codigo_internacao),
	foreign key (CNES) references estabelecimento_de_saude(CNES)
);

--  Novamente, nas tr�s tabelas abaixo foi necess�rio utilizar a restri��o,
--  isso �, estamos assumindo que a chave estrangeira "c�digo" nas tabelas abaixo
--  v�o ser unicas.

create table teste_antigenico(
	codigo int,
	resultado_final varchar(30),
	tipo varchar(30),
	resultado_antigenico varchar(30),
	influenza varchar(30),
	sars_cov_2 varchar(10), -- sim ou n�o? bool?
	vsr varchar(10), 
	parainfluenza1 varchar(10), 
	parainfluenza2 varchar(10),
	parainfluenza3 varchar(10),
	adenovirus varchar(10),
	outro_virus varchar(60),
	constraint anti_codigo unique (codigo),
	foreign key (codigo) references teste(codigo)
);

create table teste_rtcpcr(
	codigo int,
	resultado_final varchar(30),
	resultado_ptpcr varchar(30),
	influenza varchar(30),
	influenza_subtipo varchar(30),
	influenza_linhagem varchar(30),
	sars_cov_2 varchar(10),
	vsr varchar(10),
	parainfluenza1 varchar(10), 
	parainfluenza2 varchar(10), 
	parainfluenza3 varchar(10), 
	parainfluenza4 varchar(10), 
	adenovirus varchar(10),
	metapneumovirus varchar(10),
	bocavirus varchar(10),
	rinovirus varchar(10),
	outro_virus varchar(60),
	constraint rtcpcr_codigo unique (codigo),
	foreign key (codigo) references teste(codigo)
);

create table teste_sorologico(
	codigo int,
	resultado_final varchar(30),
	tipo_amostra varchar(30),
	tipo_sorologia varchar(30),
	resultado_sorologia varchar(30),
	resultado_igg varchar(30),
	resultado_iga varchar(30),
	resultado_igm varchar(30),
	constraint soro_codigo unique (codigo),
	foreign key (codigo) references teste(codigo)
);

create table realizacao_antigenico_laboratorio(
	CNES char(7),
	codigo int,
	foreign key (CNES) references estabelecimento_de_saude(CNES),
	foreign key (codigo) references teste_antigenico(codigo)
);

create table realizacao_rtcpcr_laboratorio(
	CNES char(7),
	codigo int,
	foreign key (CNES) references estabelecimento_de_saude(CNES),
	foreign key (codigo) references teste_rtcpcr(codigo)
);


-- ################################################
-- ####### ideias / propostas de consultas: #######
-- ################################################

-- apenas ideias, n�o tenho certeza se est� correto sem testar

-- mostrar os pa�ses com maior numero de mortes em ordem decrescente
-- seja "data_atual" a ultima data dispon�vel no arquivo de dados (sem ela, seria preciso somar 
-- a coluna de "novos" para cada pa�s?)

select nome, quantidade_total
from informacoes_globais_casos ifg natural join pais natural join ente_federativo 
where ifg.datas = "data_atual"
order by quantidade_total desc

-- mostrar o pa�s com maior numero de mortes a cada milh�o. (compara��o com item acima?)

select nome, total_por_milhao
from informacoes_globais_casos ifg natural join pais natural join ente_federativo 
where ifg.datas = "data_atual"
order by total_por_milhao desc

-- obter as informa��es (numero de casos, mortes, vacina��es e hospitaliza��es) de cada pa�s do in�cio at� os dias atuais.
-- fazer essa opera��o ficou trabalhoso demais?

select nome, C.datas, C.quantidade_total Casos, M.quantidade_total Mortes, H.quantidade_total Hospitalizacoes, V.quantidade_total Vacinacoes
from (ente_federativo natural join pais) base, informacoes_globais_mortes M,
	 informacoes_globais_casos C, informacoes_globais_hospitalizacoes H, informacoes_globais_vacinacoes V
where base.ID = C.paisID and base.id = M.paisID and base.ID = H.paisID and base.ID = V.paisID


-- obter as informa��es (numero de casos, mortes, vacina��es e hospitaliza��es, etc...) TOTAL de cada estado do brasil
-- mostrar do maior para o menor em numero de casos...

select nome, casos_confirmados, mortes, pedidos_por_leito, taxa_mortalidade
from ente_federativo natural join estado natural join relatorio_covid
where datas = "data_de_hoje"
order by casos_confirmados desc

-- Fazer uma consulta relacionada ao numero de leitos oculpados...

--################################################################
-- ####### consultas que precisam de processamento em java? ######

-- obter as informa��es (numero de casos, mortes, vacina��es e hospitaliza��es) de um pa�s digitado pelo usu�rio,
-- mesmo para estado e munic�pio

-- obter informa��es de quantos leitos est�o desocupados em um dado hospital
-- obter informa��es at� uma dada data?
