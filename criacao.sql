create table ente_federativo(
	codigo int,
	nome varchar(100) not null,
	pib float,
	populacao int not null,
	primary key(codigo)
);

create table estado(
	codigo int,
	sigla varchar(2),
	foreign key (codigo) references ente_federativo(codigo),
	UNIQUE(codigo)
);

create table municipio(
	codigo int,
	estadoID int,
	foreign key (codigo) references ente_federativo(codigo),
	foreign key (estadoID) references ente_federativo(codigo),
	constraint colunas_diferentes check (codigo <> estadoID),
	UNIQUE(codigo)
);

create table pais(
	codigo int,
	sigla varchar(8),
	foreign key (codigo) references ente_federativo(codigo),
	UNIQUE(codigo)
);

create table informacoes_globais_mortes(
	paisID int,
	datas date not null, -- escrito "datas" pois "data" � palavra reservada.
	novos int,
	quantidade_total int,
	novos_por_milhao float,
	total_por_milhao float,
	foreign key (paisID) references pais(codigo)
);

create table informacoes_globais_casos(
	paisID int,
	datas date not null,
	novos int,
	quantidade_total int,
	novos_por_milhao float,
	total_por_milhao float,
	foreign key (paisID) references pais(codigo)
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
	foreign key (paisID) references pais(codigo)
);

create table informacoes_globais_vacinacoes(
	paisID int,
	datas date not null,
	novos int,
	quantidade_total int,
	total_por_centena float,
	foreign key (paisID) references pais(codigo)
);

create table informacoes_globais_hospitalizacoes(
	paisID int,
	datas date not null,
	quantidade_total int,
	pacientes_uti int,
	pacientes_uti_por_milhao float,
	admissoes_semanais_uti float,
	admissoes_semanais float,
	foreign key (paisID) references pais(codigo)
);

create table endereco(
	CEP varchar(8),
	estado int,
	municipio int,
	rua varchar(100),
	bairro varchar(100),
	complemento varchar(100),
	primary key (CEP),
	foreign key (estado) references estado(codigo),
	foreign key (municipio) references municipio(codigo)
);

create table estabelecimento_de_saude(
	CNES varchar(7), -- Inicialmente feito com int, mas depois pensei nos 0s a esquerda.
	nome varchar(200) not null,
	cep varchar(8), -- xxxxx-xxx ou xxxxxxxx
	primary key (CNES)
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

create table registro_de_ocupacao(
	CNES varchar(7),
	codigo int,
	foreign key (CNES) references estabelecimento_de_saude(CNES),
	foreign key (codigo) references registro(codigo)
);




COPY ente_federativo FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\ente_federativo.csv' CSV encoding 'windows-1251';
COPY pais FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\pais.csv' CSV encoding 'windows-1251';
COPY estado FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\estado.csv' CSV encoding 'windows-1251';
COPY municipio FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\municipio.csv' CSV encoding 'windows-1251';

COPY informacoes_globais_mortes FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\informacoes_globais_mortes.csv' CSV encoding 'windows-1251';
COPY informacoes_globais_casos FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\informacoes_globais_casos.csv' CSV encoding 'windows-1251';
COPY informacoes_globais_testes FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\informacoes_globais_testes.csv' CSV encoding 'windows-1251';
COPY informacoes_globais_vacinacoes FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\informacoes_globais_vacinacoes.csv' CSV encoding 'windows-1251';
COPY informacoes_globais_hospitalizacoes FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\informacoes_globais_hospitalizacoes.csv' CSV encoding 'windows-1251';

COPY endereco FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\enderecos.csv' CSV encoding 'windows-1251';
COPY estabelecimento_de_saude FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\estabelecimento_de_saude.csv' CSV encoding 'windows-1251';
COPY registro FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\registro.csv' CSV encoding 'windows-1251';
COPY registro_de_ocupacao FROM 'C:\Users\olipp\Documents\GitHub\ProjetoBD\data\tabelas\registro_de_ocupacao.csv' CSV encoding 'windows-1251';

