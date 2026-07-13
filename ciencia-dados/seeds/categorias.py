categorias_seed = [
  {
    "categoria": "Alimentação",
    "descricoes": [
      "Supermercado", "supermerc", "iFood", "Restaurante", "Padaria",
      "Lanche na padaria", "Feira livre", "Marmita", "Almoço no self-service",
      "Hortifruti", "Açougue", "Pizza delivery 🍕", "Mercadinho da esquina",
      "Café da manhã", "Rappi comida", "Sacolão", "Restaurante japonês",
      "Boteco", "Padoca", "Doceria", "Hamburgueria", "Sushi", "Lanchonete",
      "Quentinha", "Churrascaria", "Cantina", "Confeitaria", "Mercado Extra",
      "Pão de Açúcar", "Carrefour compras", "Assaí atacadista", "Big compras mês",
      "Sorveteria", "Cafeteria", "Starbucks", "Delivery japonês",
      "Rodízio de pizza", "Comida japonesa", "Empório", "Casa de sucos",
      "Tapiocaria", "Food truck", "Feira do produtor", "Mercearia",
      "Compra do mês", "Almoço executivo", "Ceia de natal", "Café da tarde",
      "Kg de carne açougue", "Padaria pão quentinho", "Sorvete no shopping"
    ],
    "faixa_valor": {"min": 400, "max": 2000},
    "armadilhas": [
      {"descricao": "Farmácia e Mercado", "pode_confundir_com": "Saúde", "motivo": "compra combinada em supermercados com farmácia interna"},
      {"descricao": "Padaria - pão e remédio", "pode_confundir_com": "Saúde", "motivo": "padarias vendem itens de conveniência incluindo medicamentos simples"},
      {"descricao": "Jantar de aniversário", "pode_confundir_com": "Lazer", "motivo": "refeição associada a comemoração/evento social"}
    ]
  },
  {
    "categoria": "Transporte",
    "descricoes": [
      "Uber", "99 ida trab", "Gasolina", "Posto Ipiranga", "Ônibus",
      "Estacionamento shopping", "Pedágio", "Metrô", "Bilhete único",
      "Revisão do carro", "Táxi", "Uber moto", "Seguro do carro",
      "Troca de óleo", "Multa de trânsito", "IPVA", "Combustível",
      "Recarga bilhete", "InDriver", "Patinete elétrico", "Uber volta casa",
      "Posto Shell", "Estacionamento rotativo", "Zona azul", "Alinhamento e balanceamento",
      "Troca de pneu", "Bicicleta compartilhada", "Van escolar", "Fretado empresa",
      "Aplicativo de transporte", "Motoboy próprio", "Aluguel de carro",
      "Locadora de veículo", "Guincho", "Seguro de moto", "Combustível moto",
      "Estacionamento aeroporto", "Passagem de ônibus intermunicipal",
      "Bilhete metrô recarga", "Uber pra faculdade", "Corrida de app",
      "Vistoria veicular", "Lavagem do carro", "Carro por assinatura",
      "Multa de estacionamento", "Seguro DPVAT", "Documentação do carro",
      "Transporte escolar", "Carona paga", "Corrida 99"
    ],
    "faixa_valor": {"min": 4, "max": 500},
    "armadilhas": [
      {"descricao": "IPVA", "pode_confundir_com": "Dívidas", "motivo": "é um imposto obrigatório, pode parecer taxa/dívida em vez de custo de transporte"},
      {"descricao": "Seguro do carro", "pode_confundir_com": "Serviços", "motivo": "seguro é uma prestação de serviço recorrente, não desgaste direto do veículo"},
      {"descricao": "Uber para o hospital", "pode_confundir_com": "Saúde", "motivo": "o motivo da viagem é de saúde, mas o gasto em si é de transporte"}
    ]
  },
  {
    "categoria": "Saúde",
    "descricoes": [
      "Farmácia", "Droga Raia", "Consulta médica", "Plano de saúde",
      "Academia", "acad", "Exame de sangue", "Fisioterapia",
      "Consulta psicólogo", "Dentista", "Óculos de grau", "Remédio",
      "Vacina", "Nutricionista", "Suplemento", "Whey protein 💪",
      "Consulta oftalmo", "Personal trainer", "Exame de imagem", "Vitaminas",
      "Droga Raia farmácia", "Drogasil", "Pague Menos", "Consulta dermato",
      "Exame de rotina", "Check-up anual", "Terapia semanal", "Ortopedista",
      "Cirurgia eletiva", "Aparelho ortodôntico", "Clareamento dental",
      "Aplicação de botox", "Sessão de acupuntura", "Pilates", "Yoga aula",
      "Massoterapia", "Consulta ginecologista", "Consulta pediatra filho",
      "Homeopatia", "Consulta nutricional", "Aparelho auditivo",
      "Lente de contato", "Protetor solar farmácia", "Teste covid",
      "Vacina gripe", "Plano odontológico", "Fralda geriátrica",
      "Cadeira de rodas aluguel", "Fonoaudiólogo", "Consulta psiquiátrica"
    ],
    "faixa_valor": {"min": 15, "max": 1000},
    "armadilhas": [
      {"descricao": "Whey protein", "pode_confundir_com": "Alimentação", "motivo": "é um suplemento alimentar, pode ser confundido com compra de mercado"},
      {"descricao": "Personal trainer", "pode_confundir_com": "Lazer", "motivo": "atividade física pode ser vista como hobby em vez de cuidado com saúde"},
      {"descricao": "Plano de saúde", "pode_confundir_com": "Assinaturas", "motivo": "é uma mensalidade recorrente, parecido com estrutura de assinatura"}
    ]
  },
  {
    "categoria": "Moradia",
    "descricoes": [
      "Aluguel", "Condomínio", "Conta de luz", "Conta de água", "Conta de gás",
      "Internet residencial", "Mobília", "Reforma", "Material de construção",
      "IPTU", "Taxa de condomínio", "Móveis planejados", "Pintura da casa",
      "Encanamento", "Seguro residencial", "Portão eletrônico", "Cortina",
      "Ar-condicionado", "Chaveiro emergencial", "Dedetização",
      "Botijão de gás", "Conta de luz Enel", "Conta Sabesp", "Faxina pesada",
      "Colchão novo", "Sofá novo", "Eletrodoméstico", "Geladeira nova",
      "Fogão novo", "Máquina de lavar", "Persiana", "Tapete", "Armário embutido",
      "Instalação elétrica", "Troca de fechadura", "Vaso sanitário",
      "Torneira nova", "Piso novo", "Telhado conserto", "Jardinagem",
      "Manutenção do portão", "Câmera de segurança", "Alarme residencial",
      "Interfone", "Aluguel de imóvel comercial", "Taxa de água condomínio",
      "Rateio extra condomínio", "Síndico profissional", "Seguro incêndio",
      "Reforma do banheiro"
    ],
    "faixa_valor": {"min": 40, "max": 2800},
    "armadilhas": [
      {"descricao": "Encanamento", "pode_confundir_com": "Serviços", "motivo": "conserto de encanamento é mão de obra, pode parecer serviço avulso"},
      {"descricao": "Chaveiro emergencial", "pode_confundir_com": "Serviços", "motivo": "é prestação pontual de serviço, não custo fixo de moradia"},
      {"descricao": "Seguro residencial", "pode_confundir_com": "Assinaturas", "motivo": "cobrança mensal recorrente parecida com assinatura"}
    ]
  },
  {
    "categoria": "Educação",
    "descricoes": [
      "Mensalidade faculdade", "Curso online", "Escola dos filhos",
      "Material didático", "Curso de inglês", "Livro técnico",
      "Mensalidade escolar", "Certificação profissional", "Udemy",
      "Curso técnico", "Apostila", "Aula particular", "Curso de programação",
      "Pós-graduação", "MBA", "Papelaria escolar", "Uniforme escolar",
      "Kumon", "Curso preparatório", "Inscrição em concurso",
      "Mensalidade cursinho", "Curso de espanhol", "Aula de reforço",
      "Faculdade EAD", "Mensalidade pós", "Curso de excel", "Curso de design",
      "Livro de faculdade", "Xerox apostila", "Inscrição vestibular",
      "Taxa de matrícula", "Curso de culinária", "Curso de música",
      "Aula de violão", "Aula de canto", "Curso de fotografia",
      "Workshop profissional", "Congresso acadêmico", "Palestra paga",
      "Curso de libras", "Mensalidade intercâmbio", "Aula de reforço matemática",
      "Kit escolar", "Mochila escolar", "Livro paradidático",
      "Assinatura biblioteca digital", "Curso de marketing digital",
      "Certificação AWS", "Curso de dados", "Mensalidade curso técnico"
    ],
    "faixa_valor": {"min": 20, "max": 2000},
    "armadilhas": [
      {"descricao": "Papelaria escolar", "pode_confundir_com": "Outras", "motivo": "compra pontual de material pode parecer gasto avulso genérico"},
      {"descricao": "Curso de inglês", "pode_confundir_com": "Assinaturas", "motivo": "cursos online por assinatura mensal (ex: apps de idiomas) se confundem com assinatura pura"},
      {"descricao": "Uniforme escolar", "pode_confundir_com": "Outras", "motivo": "compra de vestuário específico pode ser categorizada como item avulso"}
    ]
  },
  {
    "categoria": "Lazer",
    "descricoes": [
      "Cinema", "Jogo digital", "Livro", "Show", "Viagem", "Passeio no parque",
      "Bar com amigos", "Parque de diversões", "Ingresso de evento",
      "Balada", "Steam sale 🎮", "Aluguel de filme", "Boliche",
      "Karaokê", "Passeio de barco", "Zoológico", "Museu",
      "Festival de música", "Escape room", "Compra de skin de jogo",
      "Playstation Store", "Xbox loja", "Ingresso teatro", "Parque aquático",
      "Passeio de trilha", "Acampamento", "Praia final de semana",
      "Hospedagem Airbnb", "Passagem aérea viagem", "Aluguel de bike",
      "Pescaria", "Barzinho", "Happy hour", "Festa junina",
      "Ingresso de circo", "Kart", "Sinuca", "Jogo de tabuleiro",
      "Cervejaria artesanal", "Vinho especial", "Compra de vinil",
      "Convenção de games", "Comic con ingresso", "Passeio ciclístico",
      "Aluguel de casa de praia", "Passeio de lancha", "Aquário municipal",
      "Show de stand-up", "Ingresso de festival", "Camping"
    ],
    "faixa_valor": {"min": 10, "max": 1500},
    "armadilhas": [
      {"descricao": "Bar com amigos", "pode_confundir_com": "Alimentação", "motivo": "envolve consumo de comida e bebida, pode parecer gasto alimentar"},
      {"descricao": "Viagem", "pode_confundir_com": "Transporte", "motivo": "inclui passagens e deslocamento, sobreposição com custos de transporte"},
      {"descricao": "Museu", "pode_confundir_com": "Educação", "motivo": "cunho cultural/educativo pode ser interpretado como despesa educacional"}
    ]
  },
  {
    "categoria": "Serviços",
    "descricoes": [
      "Diarista", "Encanador", "Cabeleireiro", "Manicure", "Eletricista",
      "Lavanderia", "Costureira", "Chaveiro", "Pedreiro", "Jardineiro",
      "Pet shop banho e tosa", "Passadeira", "Marido de aluguel",
      "Técnico de informática", "Motoboy entrega", "Fotógrafo",
      "Serviço de mudança", "Dedetizador", "Reparo de celular", "Assistência técnica",
      "Barbeiro", "Depilação", "Design de sobrancelha", "Maquiadora",
      "Personal organizer", "Faxina semanal", "Piscineiro", "Marceneiro",
      "Vidraceiro", "Gesseiro", "Pintor de parede", "Instalador de ar-condicionado",
      "Técnico de TV", "Conserto de máquina de lavar", "Costura de roupa",
      "Ajuste de calça", "Revisão de sapato sapataria", "Gravação de evento",
      "Cerimonialista", "DJ para festa", "Buffet contratado",
      "Segurança de evento", "Motorista particular", "Tradutor freelancer",
      "Contador autônomo", "Advogado consulta", "Consultoria financeira",
      "Serviço de streaming de conteúdo próprio", "Manutenção de piscina",
      "Instalação de câmeras", "Aula de reforço doméstica"
    ],
    "faixa_valor": {"min": 15, "max": 500},
    "armadilhas": [
      {"descricao": "Pet shop banho e tosa", "pode_confundir_com": "Outras", "motivo": "sem categoria de 'pets' dedicada, pode ser mal classificado como item genérico"},
      {"descricao": "Reparo de celular", "pode_confundir_com": "Outras", "motivo": "conserto de eletrônico pode parecer compra avulsa não recorrente"},
      {"descricao": "Dedetizador", "pode_confundir_com": "Moradia", "motivo": "está ligado à manutenção da casa, podendo ser visto como custo de moradia"}
    ]
  },
  {
    "categoria": "Assinaturas",
    "descricoes": [
      "Netflix", "Spotify", "iCloud", "Amazon Prime", "Disney+",
      "YouTube Premium", "HBO Max", "Software de edição", "Adobe Creative Cloud",
      "Xbox Game Pass", "PlayStation Plus", "Globoplay", "Deezer",
      "Apple Music", "Dropbox", "Canva Pro", "ChatGPT Plus", "Duolingo Plus",
      "Kindle Unlimited", "Paramount+", "Crunchyroll", "Star+",
      "Google One", "Microsoft 365", "Notion Pro", "Spotify Family",
      "Apple TV+", "Twitch subscription", "Discord Nitro", "iFood clube",
      "Amazon Music", "Audible", "Assinatura de revista digital",
      "VPN premium", "Antivírus anual", "Assinatura de app de treino",
      "Assinatura clube de vinhos", "Clube de assinatura de livros",
      "Assinatura barbearia mensal", "Peloton app", "Headspace premium",
      "Calm app assinatura", "Tinder Gold", "LinkedIn Premium",
      "Assinatura de jornal online", "Globo News assinatura",
      "Steam Deck plano", "Nuuvem clube", "Xbox Live Gold",
      "Assinatura de café mensal", "Clube de assinatura de queijos"
    ],
    "faixa_valor": {"min": 9, "max": 150},
    "armadilhas": [
      {"descricao": "Xbox Game Pass", "pode_confundir_com": "Lazer", "motivo": "é uma assinatura, mas o conteúdo consumido (jogos) remete a lazer"},
      {"descricao": "Adobe Creative Cloud", "pode_confundir_com": "Educação", "motivo": "pode ser usado para fins de estudo/profissionalização, gerando ambiguidade de propósito"},
      {"descricao": "ChatGPT Plus", "pode_confundir_com": "Serviços", "motivo": "ferramenta de produtividade paga pode parecer prestação de serviço"}
    ]
  },
  {
    "categoria": "Dívidas",
    "descricoes": [
      "Financiamento do carro", "Financiamento da casa", "Fatura antiga do cartão",
      "Juros de empréstimo", "Taxa de cancelamento", "Parcela de empréstimo pessoal",
      "Renegociação de dívida", "Empréstimo consignado", "Cheque especial",
      "Juros rotativo cartão", "Acordo com credor", "Parcelamento de dívida",
      "Empréstimo com amigo", "Dívida em atraso", "Multa contratual",
      "Empréstimo estudantil", "Financiamento de móveis", "Consórcio",
      "Empréstimo no banco", "Refinanciamento", "Parcela do celular financiado",
      "Cartão de loja parcelado", "Empréstimo via app", "Dívida com fornecedor",
      "Acordo trabalhista pagamento", "Parcela do computador financiado",
      "Empréstimo para reforma", "Juros de atraso boleto", "Negativação renegociada",
      "Empréstimo consignado INSS", "Financiamento estudantil FIES",
      "Parcelamento de dívida fiscal", "Dívida antiga de faculdade",
      "Empréstimo pessoal banco digital", "Juros de cartão internacional",
      "Parcela do celular", "Dívida de cartão em atraso", "Empréstimo emergencial",
      "Empréstimo para capital de giro", "Cobrança judicial parcela",
      "Acordo extrajudicial", "Empréstimo peer-to-peer", "Dívida de faculdade particular",
      "Parcelamento de multa de trânsito", "Empréstimo garantido por imóvel",
      "Dívida antiga renegociada banco", "Empréstimo para viagem parcelado",
      "Financiamento de equipamento", "Parcela atrasada loja", "Empréstimo familiar"
    ],
    "faixa_valor": {"min": 40, "max": 4000},
    "armadilhas": [
      {"descricao": "Financiamento de móveis", "pode_confundir_com": "Moradia", "motivo": "compra relacionada à casa, mas o gasto em si é a parcela de uma dívida"},
      {"descricao": "Consórcio", "pode_confundir_com": "Outras", "motivo": "consórcio tem características de poupança programada, ambíguo entre dívida e investimento"},
      {"descricao": "Empréstimo estudantil", "pode_confundir_com": "Educação", "motivo": "está ligado a estudos, mas o gasto é o pagamento da dívida, não a mensalidade em si"}
    ]
  },
  {
    "categoria": "Investimentos",
    "descricoes": [
      "Aporte Tesouro Direto", "Compra de ações", "Aplicação CDB", "Aporte fundo imobiliário",
      "Compra de criptomoeda", "Aporte previdência privada", "Aplicação poupança",
      "Compra de ETF", "Aporte fundo multimercado", "Compra de dólar investimento",
      "Aporte mensal carteira", "Aplicação LCI", "Aplicação LCA", "Compra de Bitcoin",
      "Compra de Ethereum", "Aporte renda fixa", "Aporte ações internacionais",
      "Compra de FIIs", "Aplicação em COE", "Aporte clube de investimento",
      "aporte mensal", "compra acoes", "tesouro direto", "invest mensal",
      "Aplicação CDB banco digital", "Compra de fundo de índice", "Aporte previdência PGBL",
      "Aporte previdência VGBL", "Compra de ações XP", "Compra de ações Nubank",
      "Aplicação em debêntures", "Aporte fundo de ações", "Compra de ouro investimento",
      "Aporte carteira internacional", "Transferência para corretora", "Aplicação CDI",
      "Compra de ações Vale", "Compra de ações Petrobras", "Aporte fundo cambial",
      "Investimento em startups", "Aplicação em fundo de previdência",
      "Compra de moedas estrangeiras investimento", "Aporte reserva de emergência",
      "Compra de NFT investimento", "Aporte plano de aposentadoria",
      "Aplicação renda fixa prefixada", "Aporte fundo de crédito privado",
      "Compra de títulos públicos", "Investimento em consórcio de imóvel",
      "Transferência para conta investimento"
    ],
    "faixa_valor": {"min": 20, "max": 50000},
    "armadilhas": [
      {"descricao": "Investimento em consórcio de imóvel", "pode_confundir_com": "Moradia", "motivo": "Envolve imóvel, mas trata-se de uma aplicação/investimento financeiro, não despesa de moradia"},
      {"descricao": "Compra de ouro investimento", "pode_confundir_com": "Outras", "motivo": "Pode parecer compra de bem/joia, mas a finalidade declarada é investimento financeiro"},
      {"descricao": "Investimento em startups", "pode_confundir_com": "Serviços", "motivo": "Pode ser confundido com contratação de consultoria/serviço, mas trata-se de aporte de capital"}
    ]
  },
  {
    "categoria": "Outras",
    "descricoes": [
      "Saque em caixa eletrônico", "Transferência Pix", "Presente de aniversário",
      "Doação", "Compra diversa", "Multa", "Taxa bancária", "Tarifa de manutenção de conta",
      "Rifa", "Aposta esportiva", "Vaquinha online", "Compra em brechó",
      "Item perdido/reembolso", "Troco não identificado", "Câmbio de moeda",
      "Compra internacional não identificada", "Doação para ONG",
      "Presente de casamento", "Contribuição sindical", "Taxa de cartório",
      "Saque emergencial", "Pix não identificado", "Transferência TED",
      "Compra em bazar", "Empréstimo a amigo", "Ajuda financeira família",
      "Compra em leilão", "Cota de rateio de churrasco", "Presente de dia das mães",
      "Presente de natal", "Doação de campanha", "Vaquinha do trabalho",
      "Compra de rifa beneficente", "Reembolso de viagem", "Estorno não identificado",
      "Taxa de emissão de documento", "Segunda via de documento",
      "Taxa de serviço bancário", "Multa de biblioteca", "Compra de bilhete de loteria",
      "Doação de sangue custo transporte", "Contribuição condominial extra",
      "Taxa de associação de bairro", "Anuidade de clube",
      "Taxa de inscrição em sorteio", "Compra não reconhecida",
      "Cobrança duplicada estorno", "Presente surpresa", "Doação para vaquinha médica",
      "Compra em feira de trocas"
    ],
    "faixa_valor": {"min": 5, "max": 600},
    "armadilhas": [
      {"descricao": "Presente de aniversário", "pode_confundir_com": "Lazer", "motivo": "presentes têm caráter social/festivo, podendo remeter a lazer"},
      {"descricao": "Taxa de cartório", "pode_confundir_com": "Serviços", "motivo": "envolve pagamento por um serviço prestado, ambíguo com a categoria Serviços"},
      {"descricao": "Aposta esportiva", "pode_confundir_com": "Lazer", "motivo": "é uma atividade de entretenimento, mas com natureza financeira de risco distinta"}
    ]
  }
]