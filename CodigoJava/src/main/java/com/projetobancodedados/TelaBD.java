package main.java.com.projetobancodedados;

import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import org.w3c.dom.css.RGBColor;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.TableColumn;
import net.proteanit.sql.*;

public class TelaBD extends javax.swing.JFrame {
    
    public Connection con;
    
    public TelaBD() {
        initComponents();
        btnBrasil.setBackground(Color.black);
        btnMundo.setBackground(Color.gray);
    }
    public void iniciarPagina() {
        txtErro.setVisible(false);  //OCULTA MENSAGEM DE ERRO PRÓXIMA AO BOTÃO "ENTRAR"
        
        imgTelaDeUsuario.setVisible(false); //OCULTA A TELA DO USUÁRIO
        painelSetBD.setVisible(true);   //APRESENTA A TELA INICIAL DE LOGIN
        
        painelBotoes.setVisible(false); //OCULTA O PAINEL DE BOTÕES
        
        //OCULTA OS TEXTOS REFERENTES AO PROJETO
        txtBD1.setVisible(false);
        txtBD2.setVisible(false);
        
        //OCULTA OS OUTROS TRÊS PAINEIS PRINCIPAIS
        painelMundo.setVisible(false);
        painelBrasil.setVisible(false);
        
        //OCULTA A OPÇÃO MUNICIPAL
        jComboBoxMunicipio.setVisible(false);
        txtMunicipio.setVisible(false);
        
        //OCULTA AS OPÇÕES DE REGISTROS
        
    }
  
    public void inicializarBD () throws SQLException{
        painelSetBD.setVisible(false);   //OCULTA A TELA INICIAL DE LOGIN
        imgTelaDeUsuario.setVisible(true); //APRESENTA A TELA DO USUÁRIO
        
        //APRESENTA OS TEXTOS RELACIONADOS AO PROJETO
        txtBD1.setVisible(true);
        txtBD2.setVisible(true);
        
        painelBotoes.setVisible(true); //APRESENTA O PAINEL DE BOTÕES
        
        painelBrasil.setVisible(true);  //APRESENTA A TELA INICIAL, QUE CORRESPONDE AOS DADOS DO BRASIL
        
        
        
        //INICIALIZA A PÁGINA "BRASIL"
        String query = "select * from estado order by sigla";
        updateComboBox(jComboBoxUF, query, "sigla");
        query = "SELECT * FROM municipios_por_estado('"+ jComboBoxUF.getSelectedItem().toString() +"')";
        updateComboBox(jComboBoxMunicipio, query, "nome");
        
        //INICIALIZA A PÁGINA "MUNDO"
        query = "SELECT codigo, sigla, nome FROM pais NATURAL JOIN ente_federativo";
        updateComboBox(jComboBoxPAIS, query, "nome");
        
        jComboBoxPAIS.addItem("TODOS");
        
        updateTableMundo(jTabelaMundo);
    }
    
    public void updateTable(javax.swing.JTable tabela) throws SQLException {
        
        String municipio = jComboBoxMunicipio.getSelectedItem().toString();
        
        String estado = jComboBoxUF.getSelectedItem().toString();
        
        
        String periodo = "";
        int periodoConsulta = jComboBoxTipoRegistro.getSelectedIndex();
        
        switch(periodoConsulta) {
            case 0:
                periodo = "SELECT *";
                jComboBoxOrdem.setVisible(true);
                jComboBoxFiltrar.setVisible(true);
                txtFiltrarPor.setVisible(true);
                txtOrdem.setVisible(true);
                jComboBoxUF.setVisible(true);
                txtUF.setVisible(true);
                break;
            case 1:
                periodo = "SELECT SUM(clinica_ocup_suspeita) AS clinica_ocup_suspeita," +
                            " SUM(clinica_ocup_confirmado) AS clinica_ocup_confirmado," +
                            " SUM(uti_ocup_suspeita) AS uti_ocup_suspeita," +
                            " SUM(uti_ocup_confirmado) AS uti_ocup_confirmado," +
                            " SUM(obitos_suspeita) AS obitos_suspeita," +
                            " SUM(obitos_confirmado) AS obitos_confirmado," +
                            " SUM(alta_suspeita) AS alta_suspeita," +
                            " SUM(alta_confirmado) AS alta_confirmado";
                jComboBoxOrdem.setVisible(false);
                jComboBoxFiltrar.setVisible(false);
                txtFiltrarPor.setVisible(false);
                txtOrdem.setVisible(false);
                jComboBoxUF.setVisible(true);
                txtUF.setVisible(true);
                break;
            case 2:
                periodo = "SELECT * FROM total_registros_";
                
                if (jComboBoxTipoDado.getSelectedIndex() == 0) {
                    jComboBoxUF.setVisible(false);
                    txtUF.setVisible(false);
                } else {
                    jComboBoxUF.setVisible(true);
                    txtUF.setVisible(true);
                }
                jComboBoxOrdem.setVisible(true);
                jComboBoxFiltrar.setVisible(true);
                txtFiltrarPor.setVisible(true);
                txtOrdem.setVisible(true);
            default:
                break;
        }
        
        
        
        int index = jComboBoxFiltrar.getSelectedIndex();
        String filtro = "";
        
        switch (index) {
            case 0:
                filtro = "alta_confirmado";
                break;
            case 1:
                filtro = "obitos_confirmado";
                break;
            case 2:
                filtro = "clinica_ocup_confirmado";
                break;
            default: break;
        }
        
        String ordem = "";
        int ordemIndex = jComboBoxOrdem.getSelectedIndex();
        
        switch(ordemIndex) {
            case 0:
                ordem = "ASC";
                break;
            case 1:
                ordem = "DESC";
                break;
            default: break;
        }
        
        int tipo = jComboBoxTipoDado.getSelectedIndex();
        String query = "";
        
        
        switch (tipo) {
            case 0:
                if (periodoConsulta != 2) {
                    query = periodo + " FROM registros_por_estado('" + estado + "') order by "+ filtro + " "+ ordem;
                } else {
                    query = periodo + "estados() order by " + filtro + " "+ ordem;
                }
                jComboBoxMunicipio.setVisible(false);
                txtMunicipio.setVisible(false);
                break;
            case 1:
                if (periodoConsulta != 2)
                    query = periodo + " FROM registros_por_municipio('"+ municipio +"') order by "+ filtro + " "+ ordem;
                else
                    query = periodo + "municipios('"+ estado +"') order by "+ filtro + " "+ ordem;
                jComboBoxMunicipio.setVisible(true);
                txtMunicipio.setVisible(true);
                break;
            default: 
                break;
        }
        
        
        
        
        
        
        PreparedStatement stat = con.prepareStatement(query);
        ResultSet rs= stat.executeQuery();
        
        tabela.setModel(DbUtils.resultSetToTableModel(rs));
        
    }
    
    public void updateComboBox(javax.swing.JComboBox comboBox, String query, String columnName) throws SQLException {
        
        comboBox.removeAllItems();
        
        
        PreparedStatement stat = con.prepareStatement(query);
        
        ResultSet rs= stat.executeQuery();
        
        while(rs.next()) {
            comboBox.addItem(rs.getString(columnName));
        }
        
        
    }
    
    
    public void updateTableMundo(javax.swing.JTable tabela) throws SQLException {
        String pais = jComboBoxPAIS.getSelectedItem().toString();
        
        
        int index = jComboBoxTipoInformacao.getSelectedIndex();
        int ordemIndex = jComboBoxOrdemPais.getSelectedIndex();
        String filtro = "";
        String ordem = "";
        String query = "";
        
        if (jComboBoxPAIS.getSelectedItem() != "TODOS") {
            
            
            
            if (jComboBoxTipoInformacao.getItemCount()>=6) {
                jCheckBox1.setSelected(false);
                jComboBoxTipoInformacao.removeItemAt(5);
                jComboBoxTipoInformacao.removeItemAt(4);
                txtTipoDeInformacao.setText("TIPO DE INFORMAÇÃO");
                jComboBoxTipoInformacao.setSelectedIndex(0);
                index = jComboBoxTipoInformacao.getSelectedIndex();
                
            }
            
            switch (index) {
                case 0:
                    filtro = "SELECT * FROM informacoes_casos_por_pais('";
                    break;
                case 1:
                    filtro = "SELECT * FROM informacoes_mortes_por_pais('";
                    break;
                case 2:
                    filtro = "SELECT * FROM informacoes_testes_por_pais('";
                    break;
                case 3:
                    filtro = "SELECT * FROM informacoes_vacinacao_por_pais('";
                    break;
                default: break;
            }

            switch(ordemIndex) {
                case 0:
                    ordem = "ASC";
                    break;
                case 1:
                    ordem = "DESC";
                    break;
                default: break;
            }

            
            query = filtro + pais + "') order by datas " + ordem;
        } else {
            
            if (jComboBoxTipoInformacao.getItemCount() == 4) {
                jCheckBox1.setSelected(true);
                jComboBoxTipoInformacao.addItem("País");
                jComboBoxTipoInformacao.addItem("Hospitalizações");
                jComboBoxTipoInformacao.setSelectedIndex(4);
                index = jComboBoxTipoInformacao.getSelectedIndex();
                txtTipoDeInformacao.setText("FILTRAR POR");
            }
            
            
            switch (index) {
                case 0:
                    filtro = "casos";
                    break;
                case 1:
                    filtro = "mortes";
                    break;
                case 2:
                    filtro = "testes";
                    break;
                case 3:
                    filtro = "vacinacoes";
                    break;
                case 4:
                    filtro = "codigo";
                    break;
                case 5:
                    filtro = "hospitalizacoes";
                default: break;
            }

            switch(ordemIndex) {
                case 0:
                    ordem = "ASC";
                    break;
                case 1:
                    ordem = "DESC";
                    break;
                default: break;
            }
            
            
            query = filtro = "SELECT * FROM informacoes_totais_globais() order by " + filtro + " " + ordem;
        }
        
        
        PreparedStatement stat = con.prepareStatement(query);
        ResultSet rs= stat.executeQuery();
        
        tabela.setModel(DbUtils.resultSetToTableModel(rs));
    }
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        painelFundoNulo = new javax.swing.JPanel();
        painelCards = new javax.swing.JPanel();
        painelBrasil = new javax.swing.JPanel();
        txtBrasil = new javax.swing.JLabel();
        txtUF = new javax.swing.JLabel();
        txtMunicipio = new javax.swing.JLabel();
        txtFiltrarPor = new javax.swing.JLabel();
        txtOrdem = new javax.swing.JLabel();
        jComboBoxMunicipio = new javax.swing.JComboBox<>();
        jComboBoxUF = new javax.swing.JComboBox<>();
        jComboBoxFiltrar = new javax.swing.JComboBox<>();
        jComboBoxOrdem = new javax.swing.JComboBox<>();
        jComboBoxTipoRegistro = new javax.swing.JComboBox<>();
        txtTipoRegistro = new javax.swing.JLabel();
        txtTipoDado = new javax.swing.JLabel();
        jComboBoxTipoDado = new javax.swing.JComboBox<>();
        btnBrasil1 = new javax.swing.JButton();
        btnBrasil4 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTabelaBrasil = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        imgFundoBrasil = new javax.swing.JLabel();
        painelMundo = new javax.swing.JPanel();
        txtPAIS = new javax.swing.JLabel();
        txtTipoDeInformacao = new javax.swing.JLabel();
        txtOrdem1 = new javax.swing.JLabel();
        jComboBoxPAIS = new javax.swing.JComboBox<>();
        jComboBoxTipoInformacao = new javax.swing.JComboBox<>();
        jComboBoxOrdemPais = new javax.swing.JComboBox<>();
        btnBrasil2 = new javax.swing.JButton();
        btnBrasil3 = new javax.swing.JButton();
        btnBrasil5 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();
        jSeparator2 = new javax.swing.JSeparator();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTabelaMundo = new javax.swing.JTable();
        txtMundo = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        imgFundoMundo = new javax.swing.JLabel();
        painelSetBD = new javax.swing.JPanel();
        username = new javax.swing.JTextField();
        database = new javax.swing.JTextField();
        password = new javax.swing.JPasswordField();
        txtErro = new javax.swing.JLabel();
        btnEntrar = new javax.swing.JButton();
        LoginDB1 = new javax.swing.JLabel();
        painelBotoes = new javax.swing.JPanel();
        btnBrasil = new javax.swing.JButton();
        btnMundo = new javax.swing.JButton();
        txtBD1 = new javax.swing.JLabel();
        txtBD2 = new javax.swing.JLabel();
        imgTelaDeUsuario = new javax.swing.JLabel();
        LoginDB = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PROJETO BANCO DE DADOS - COVID-19");
        setResizable(false);

        painelFundoNulo.setLayout(null);

        painelCards.setLayout(new java.awt.CardLayout());

        painelBrasil.setLayout(null);

        txtBrasil.setBackground(new java.awt.Color(255, 255, 255));
        txtBrasil.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtBrasil.setForeground(new java.awt.Color(254, 205, 8));
        txtBrasil.setText("BRASIL");
        painelBrasil.add(txtBrasil);
        txtBrasil.setBounds(47, 5, 250, 40);

        txtUF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtUF.setText("ESTADO");
        painelBrasil.add(txtUF);
        txtUF.setBounds(190, 50, 110, 20);

        txtMunicipio.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtMunicipio.setText("MUNICÍPIO");
        painelBrasil.add(txtMunicipio);
        txtMunicipio.setBounds(320, 50, 110, 20);

        txtFiltrarPor.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtFiltrarPor.setText("FILTRAR POR");
        painelBrasil.add(txtFiltrarPor);
        txtFiltrarPor.setBounds(160, 110, 130, 20);

        txtOrdem.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOrdem.setText("ORDEM FILTRO");
        painelBrasil.add(txtOrdem);
        txtOrdem.setBounds(420, 110, 110, 20);

        jComboBoxMunicipio.setEditable(true);
        jComboBoxMunicipio.setMaximumRowCount(100);
        jComboBoxMunicipio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMunicipioActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxMunicipio);
        jComboBoxMunicipio.setBounds(320, 70, 210, 30);

        jComboBoxUF.setEditable(true);
        jComboBoxUF.setMaximumRowCount(1000);
        jComboBoxUF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxUFActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxUF);
        jComboBoxUF.setBounds(190, 70, 120, 30);

        jComboBoxFiltrar.setMaximumRowCount(100);
        jComboBoxFiltrar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Número de contaminações", "Número de óbitos", "Número de leitos hospitalares" }));
        jComboBoxFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFiltrarActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxFiltrar);
        jComboBoxFiltrar.setBounds(160, 130, 240, 30);

        jComboBoxOrdem.setMaximumRowCount(100);
        jComboBoxOrdem.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Crescente", "Decrescente" }));
        jComboBoxOrdem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOrdemActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxOrdem);
        jComboBoxOrdem.setBounds(420, 130, 110, 30);

        jComboBoxTipoRegistro.setMaximumRowCount(100);
        jComboBoxTipoRegistro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Diário", "Total", "Comparativo" }));
        jComboBoxTipoRegistro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoRegistroActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxTipoRegistro);
        jComboBoxTipoRegistro.setBounds(20, 130, 120, 30);

        txtTipoRegistro.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoRegistro.setText("TIPO DE REGISTRO");
        painelBrasil.add(txtTipoRegistro);
        txtTipoRegistro.setBounds(20, 110, 130, 20);

        txtTipoDado.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoDado.setText("APRESENTAR DADOS");
        painelBrasil.add(txtTipoDado);
        txtTipoDado.setBounds(20, 50, 140, 20);

        jComboBoxTipoDado.setMaximumRowCount(1000);
        jComboBoxTipoDado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Estaduais", "Municipais" }));
        jComboBoxTipoDado.setSelectedIndex(1);
        jComboBoxTipoDado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoDadoActionPerformed(evt);
            }
        });
        painelBrasil.add(jComboBoxTipoDado);
        jComboBoxTipoDado.setBounds(20, 70, 150, 30);

        btnBrasil1.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil1.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil1.setText(" MÉDIA MÓVEL DE MORTES");
        btnBrasil1.setToolTipText("");
        btnBrasil1.setBorder(null);
        btnBrasil1.setBorderPainted(false);
        btnBrasil1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil1.setFocusPainted(false);
        btnBrasil1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasil1ActionPerformed(evt);
            }
        });
        painelBrasil.add(btnBrasil1);
        btnBrasil1.setBounds(550, 130, 160, 30);

        btnBrasil4.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil4.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil4.setText(" MÉDIA MÓVEL DE CASOS");
        btnBrasil4.setBorder(null);
        btnBrasil4.setBorderPainted(false);
        btnBrasil4.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil4.setFocusPainted(false);
        btnBrasil4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasil4ActionPerformed(evt);
            }
        });
        painelBrasil.add(btnBrasil4);
        btnBrasil4.setBounds(550, 70, 160, 30);
        painelBrasil.add(jSeparator1);
        jSeparator1.setBounds(10, 175, 710, 10);

        jScrollPane1.setPreferredSize(new java.awt.Dimension(458, 402));

        jTabelaBrasil.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {}
            },
            new String [] {

            }
        ));
        jTabelaBrasil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabelaBrasil.setEnabled(false);
        jScrollPane1.setViewportView(jTabelaBrasil);

        painelBrasil.add(jScrollPane1);
        jScrollPane1.setBounds(15, 185, 700, 290);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        painelBrasil.add(jPanel1);
        jPanel1.setBounds(630, 0, 100, 100);

        imgFundoBrasil.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/CARD.png"))); // NOI18N
        painelBrasil.add(imgFundoBrasil);
        imgFundoBrasil.setBounds(0, 0, 731, 525);

        painelCards.add(painelBrasil, "card5");

        painelMundo.setLayout(null);

        txtPAIS.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtPAIS.setText("PAÍS");
        painelMundo.add(txtPAIS);
        txtPAIS.setBounds(20, 50, 60, 20);

        txtTipoDeInformacao.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtTipoDeInformacao.setText("TIPO DE INFORMAÇÃO");
        painelMundo.add(txtTipoDeInformacao);
        txtTipoDeInformacao.setBounds(250, 50, 170, 20);

        txtOrdem1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtOrdem1.setText("ORDEM");
        painelMundo.add(txtOrdem1);
        txtOrdem1.setBounds(430, 50, 110, 20);

        jComboBoxPAIS.setEditable(true);
        jComboBoxPAIS.setMaximumRowCount(1000);
        jComboBoxPAIS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxPAISActionPerformed(evt);
            }
        });
        painelMundo.add(jComboBoxPAIS);
        jComboBoxPAIS.setBounds(20, 70, 210, 30);

        jComboBoxTipoInformacao.setMaximumRowCount(100);
        jComboBoxTipoInformacao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Casos", "Mortes", "Testes", "Vacinação" }));
        jComboBoxTipoInformacao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxTipoInformacaoActionPerformed(evt);
            }
        });
        painelMundo.add(jComboBoxTipoInformacao);
        jComboBoxTipoInformacao.setBounds(250, 70, 160, 30);

        jComboBoxOrdemPais.setMaximumRowCount(100);
        jComboBoxOrdemPais.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Crescente", "Decrescente" }));
        jComboBoxOrdemPais.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxOrdemPaisActionPerformed(evt);
            }
        });
        painelMundo.add(jComboBoxOrdemPais);
        jComboBoxOrdemPais.setBounds(430, 70, 110, 30);

        btnBrasil2.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil2.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil2.setText("MÉDIA MÓVEL DE CASOS");
        btnBrasil2.setBorder(null);
        btnBrasil2.setBorderPainted(false);
        btnBrasil2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil2.setFocusPainted(false);
        btnBrasil2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasil2ActionPerformed(evt);
            }
        });
        painelMundo.add(btnBrasil2);
        btnBrasil2.setBounds(560, 50, 160, 30);

        btnBrasil3.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil3.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil3.setText("MÉDIA MÓVEL PARA TODOS OS PAÍSES");
        btnBrasil3.setBorder(null);
        btnBrasil3.setBorderPainted(false);
        btnBrasil3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil3.setFocusPainted(false);
        btnBrasil3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasil3ActionPerformed(evt);
            }
        });
        painelMundo.add(btnBrasil3);
        btnBrasil3.setBounds(15, 485, 650, 30);

        btnBrasil5.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil5.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil5.setText("MÉDIA MÓVEL DE MORTES");
        btnBrasil5.setBorder(null);
        btnBrasil5.setBorderPainted(false);
        btnBrasil5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil5.setFocusPainted(false);
        btnBrasil5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasil5ActionPerformed(evt);
            }
        });
        painelMundo.add(btnBrasil5);
        btnBrasil5.setBounds(560, 90, 160, 30);

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Comparar todos os países");
        jCheckBox1.setFocusPainted(false);
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });
        painelMundo.add(jCheckBox1);
        jCheckBox1.setBounds(21, 107, 210, 22);
        painelMundo.add(jSeparator2);
        jSeparator2.setBounds(10, 132, 710, 10);

        jTabelaMundo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {}
            },
            new String [] {

            }
        ));
        jTabelaMundo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabelaMundo.setEnabled(false);
        jScrollPane2.setViewportView(jTabelaMundo);

        painelMundo.add(jScrollPane2);
        jScrollPane2.setBounds(15, 145, 700, 330);

        txtMundo.setBackground(new java.awt.Color(255, 255, 255));
        txtMundo.setFont(new java.awt.Font("SansSerif", 1, 24)); // NOI18N
        txtMundo.setForeground(new java.awt.Color(254, 205, 8));
        txtMundo.setText("MUNDO");
        painelMundo.add(txtMundo);
        txtMundo.setBounds(47, 5, 250, 40);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        painelMundo.add(jPanel2);
        jPanel2.setBounds(630, 0, 100, 100);

        imgFundoMundo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/CARD.png"))); // NOI18N
        painelMundo.add(imgFundoMundo);
        imgFundoMundo.setBounds(0, 0, 731, 525);

        painelCards.add(painelMundo, "card6");

        painelSetBD.setLayout(null);

        username.setToolTipText("Comumente \"postgres\"");
        username.setBorder(null);
        painelSetBD.add(username);
        username.setBounds(130, 160, 280, 30);

        database.setToolTipText("");
        database.setBorder(null);
        painelSetBD.add(database);
        database.setBounds(130, 235, 280, 30);

        password.setToolTipText("");
        password.setBorder(null);
        painelSetBD.add(password);
        password.setBounds(130, 310, 280, 30);

        txtErro.setForeground(new java.awt.Color(255, 255, 102));
        txtErro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txtErro.setText("Dados inválidos. Tente novamente.");
        painelSetBD.add(txtErro);
        txtErro.setBounds(130, 350, 240, 20);

        btnEntrar.setBackground(new java.awt.Color(15, 53, 189));
        btnEntrar.setForeground(new java.awt.Color(255, 255, 255));
        btnEntrar.setText("ENTRAR");
        btnEntrar.setBorder(null);
        btnEntrar.setBorderPainted(false);
        btnEntrar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setPreferredSize(new java.awt.Dimension(30, 12));
        btnEntrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntrarActionPerformed(evt);
            }
        });
        painelSetBD.add(btnEntrar);
        btnEntrar.setBounds(380, 345, 80, 30);

        LoginDB1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/DB.png"))); // NOI18N
        painelSetBD.add(LoginDB1);
        LoginDB1.setBounds(-170, 0, 900, 525);

        painelCards.add(painelSetBD, "card6");

        painelFundoNulo.add(painelCards);
        painelCards.setBounds(168, 0, 731, 525);

        painelBotoes.setBackground(new java.awt.Color(14, 90, 60));
        painelBotoes.setForeground(new java.awt.Color(255, 255, 255));

        btnBrasil.setBackground(new java.awt.Color(153, 153, 153));
        btnBrasil.setForeground(new java.awt.Color(255, 255, 255));
        btnBrasil.setText("BRASIL");
        btnBrasil.setBorder(null);
        btnBrasil.setBorderPainted(false);
        btnBrasil.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnBrasil.setFocusPainted(false);
        btnBrasil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrasilActionPerformed(evt);
            }
        });

        btnMundo.setBackground(new java.awt.Color(153, 153, 153));
        btnMundo.setForeground(new java.awt.Color(255, 255, 255));
        btnMundo.setText("MUNDO");
        btnMundo.setBorder(null);
        btnMundo.setBorderPainted(false);
        btnMundo.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnMundo.setFocusPainted(false);
        btnMundo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMundoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout painelBotoesLayout = new javax.swing.GroupLayout(painelBotoes);
        painelBotoes.setLayout(painelBotoesLayout);
        painelBotoesLayout.setHorizontalGroup(
            painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(painelBotoesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBrasil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMundo, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                .addContainerGap())
        );
        painelBotoesLayout.setVerticalGroup(
            painelBotoesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, painelBotoesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnBrasil, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addComponent(btnMundo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(221, 221, 221))
        );

        painelFundoNulo.add(painelBotoes);
        painelBotoes.setBounds(14, 190, 140, 330);

        txtBD1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtBD1.setForeground(new java.awt.Color(254, 205, 8));
        txtBD1.setText("COVID-19");
        painelFundoNulo.add(txtBD1);
        txtBD1.setBounds(10, 30, 80, 30);

        txtBD2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtBD2.setForeground(new java.awt.Color(254, 205, 8));
        txtBD2.setText("BANCO DE DADOS");
        painelFundoNulo.add(txtBD2);
        txtBD2.setBounds(10, 10, 140, 30);

        imgTelaDeUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/TELA-DE-USUARIO.png"))); // NOI18N
        painelFundoNulo.add(imgTelaDeUsuario);
        imgTelaDeUsuario.setBounds(0, 0, 900, 525);

        LoginDB.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagens/DB.png"))); // NOI18N
        painelFundoNulo.add(LoginDB);
        LoginDB.setBounds(0, 0, 900, 525);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelFundoNulo, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(painelFundoNulo, javax.swing.GroupLayout.PREFERRED_SIZE, 525, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnBrasilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasilActionPerformed
        painelBrasil.setVisible(true);
        painelMundo.setVisible(false);
        
        btnBrasil.setBackground(Color.black);
        btnMundo.setBackground(Color.gray);
    }//GEN-LAST:event_btnBrasilActionPerformed

    private void btnMundoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMundoActionPerformed
        painelBrasil.setVisible(false);
        painelMundo.setVisible(true);
        
        btnBrasil.setBackground(Color.gray);
        btnMundo.setBackground(Color.black);
        
    }//GEN-LAST:event_btnMundoActionPerformed

    private void btnEntrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntrarActionPerformed
        
        /*
        DADOS SIMPLES DE LOGIN: CAPTURA O USUÁRIO DO BANCO DE DADOS,
        O BANCO DE DADOS QUE SERÁ USADO. NESTE CASO, SERVE APENAS E ESPECIFICAMENTE PARA
        O BANCO DE DADOS SOBRE COVID-19 FEITO PELO GRUPO
        */
        
        String user = username.getText();
        String db = database.getText();
        String pw = password.getText(); /*NÃO É O IDEAL, MAS O ACESSO AO BANCO DE DADOS TEM QUE SER STRING E NÃO PODE SER ENCRIPTOGRAFADO*/
        
        
        
        try {
            String connectionString = "jdbc:postgresql://localhost/" + db;

            con = DriverManager.getConnection(connectionString, user, pw);
            
            inicializarBD();
            
        } catch (Exception e) {
            txtErro.setVisible(true);
            
            e.printStackTrace();
        }
        
        
        
    }//GEN-LAST:event_btnEntrarActionPerformed

    private void jComboBoxUFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxUFActionPerformed
        String UF = jComboBoxUF.getSelectedItem().toString();
        
        String query = "SELECT * FROM municipios_por_estado('"+UF+"')";
        
        try {
            updateComboBox(jComboBoxMunicipio, query, "nome");
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxUFActionPerformed

    private void jComboBoxMunicipioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMunicipioActionPerformed
        if (jComboBoxMunicipio.getSelectedItem() != null) {
            try {
                updateTable(jTabelaBrasil);
            } catch (SQLException ex) {
                Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jComboBoxMunicipioActionPerformed

    private void jComboBoxPAISActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxPAISActionPerformed
        try {
            updateTableMundo(jTabelaMundo);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxPAISActionPerformed

    private void jComboBoxFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFiltrarActionPerformed
  
        try {
            updateTable(jTabelaBrasil);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
    }//GEN-LAST:event_jComboBoxFiltrarActionPerformed

    private void jComboBoxOrdemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxOrdemActionPerformed
        try {
            updateTable(jTabelaBrasil);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxOrdemActionPerformed

    private void jComboBoxTipoInformacaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTipoInformacaoActionPerformed
        try {
            updateTableMundo(jTabelaMundo);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxTipoInformacaoActionPerformed

    private void jComboBoxTipoDadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTipoDadoActionPerformed
        try {
            updateTable(jTabelaBrasil);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxTipoDadoActionPerformed

    private void btnBrasil1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasil1ActionPerformed
        String query = "SELECT * FROM media_movel_mortes_pais('Brazil')";
        
        try {
            PreparedStatement stat;
            stat = con.prepareStatement(query);
            ResultSet rs= stat.executeQuery();
            jTabelaBrasil.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnBrasil1ActionPerformed

    private void jComboBoxOrdemPaisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxOrdemPaisActionPerformed
        try {
            updateTableMundo(jTabelaMundo);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxOrdemPaisActionPerformed

    private void jComboBoxTipoRegistroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxTipoRegistroActionPerformed
        try {
            updateTable(jTabelaBrasil);
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jComboBoxTipoRegistroActionPerformed

    private void btnBrasil2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasil2ActionPerformed
        
        
        
        String pais = jComboBoxPAIS.getSelectedItem().toString();
        String query = "";
        
        if (pais == "TODOS")
            query = "SELECT * FROM media_movel_paises()";
        else
            query = "SELECT * FROM media_movel_casos_pais('" + pais + "')";
        
        try {
            PreparedStatement stat;
            stat = con.prepareStatement(query);
            ResultSet rs= stat.executeQuery();
            jTabelaMundo.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnBrasil2ActionPerformed

    private void btnBrasil3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasil3ActionPerformed
        String pais = jComboBoxPAIS.getSelectedItem().toString();
        
        String query = "SELECT * FROM media_movel_paises()";
        
        try {
            PreparedStatement stat;
            stat = con.prepareStatement(query);
            ResultSet rs= stat.executeQuery();
            jTabelaMundo.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnBrasil3ActionPerformed

    private void btnBrasil4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasil4ActionPerformed
         String query = "SELECT * FROM media_movel_casos_pais('Brazil')";
        
        try {
            PreparedStatement stat;
            stat = con.prepareStatement(query);
            ResultSet rs= stat.executeQuery();
            jTabelaBrasil.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnBrasil4ActionPerformed

    private void btnBrasil5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrasil5ActionPerformed
        String pais = jComboBoxPAIS.getSelectedItem().toString();
        String query = "";
        
        if (pais == "TODOS")
            query = "SELECT * FROM media_movel_paises()";
        else
            query = "SELECT * FROM media_movel_mortes_pais('" + pais + "')";
        
        try {
            PreparedStatement stat;
            stat = con.prepareStatement(query);
            ResultSet rs= stat.executeQuery();
            jTabelaMundo.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (SQLException ex) {
            Logger.getLogger(TelaBD.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnBrasil5ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            jComboBoxPAIS.setSelectedItem("TODOS");
        } else {
            jComboBoxTipoInformacao.setSelectedIndex(0);
            jComboBoxPAIS.setSelectedIndex(0);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        
        
        
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaBD().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LoginDB;
    private javax.swing.JLabel LoginDB1;
    private javax.swing.JButton btnBrasil;
    private javax.swing.JButton btnBrasil1;
    private javax.swing.JButton btnBrasil2;
    private javax.swing.JButton btnBrasil3;
    private javax.swing.JButton btnBrasil4;
    private javax.swing.JButton btnBrasil5;
    private javax.swing.JButton btnEntrar;
    private javax.swing.JButton btnMundo;
    private javax.swing.JTextField database;
    private javax.swing.JLabel imgFundoBrasil;
    private javax.swing.JLabel imgFundoMundo;
    private javax.swing.JLabel imgTelaDeUsuario;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBoxFiltrar;
    private javax.swing.JComboBox<String> jComboBoxMunicipio;
    private javax.swing.JComboBox<String> jComboBoxOrdem;
    private javax.swing.JComboBox<String> jComboBoxOrdemPais;
    private javax.swing.JComboBox<String> jComboBoxPAIS;
    private javax.swing.JComboBox<String> jComboBoxTipoDado;
    private javax.swing.JComboBox<String> jComboBoxTipoInformacao;
    private javax.swing.JComboBox<String> jComboBoxTipoRegistro;
    private javax.swing.JComboBox<String> jComboBoxUF;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTable jTabelaBrasil;
    private javax.swing.JTable jTabelaMundo;
    private javax.swing.JPanel painelBotoes;
    private javax.swing.JPanel painelBrasil;
    private javax.swing.JPanel painelCards;
    private javax.swing.JPanel painelFundoNulo;
    private javax.swing.JPanel painelMundo;
    private javax.swing.JPanel painelSetBD;
    private javax.swing.JPasswordField password;
    private javax.swing.JLabel txtBD1;
    private javax.swing.JLabel txtBD2;
    private javax.swing.JLabel txtBrasil;
    private javax.swing.JLabel txtErro;
    private javax.swing.JLabel txtFiltrarPor;
    private javax.swing.JLabel txtMundo;
    private javax.swing.JLabel txtMunicipio;
    private javax.swing.JLabel txtOrdem;
    private javax.swing.JLabel txtOrdem1;
    private javax.swing.JLabel txtPAIS;
    private javax.swing.JLabel txtTipoDado;
    private javax.swing.JLabel txtTipoDeInformacao;
    private javax.swing.JLabel txtTipoRegistro;
    private javax.swing.JLabel txtUF;
    private javax.swing.JTextField username;
    // End of variables declaration//GEN-END:variables
}
