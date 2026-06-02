#set par(first-line-indent: 0pt)

= Resumo

Este artigo apresenta o desenvolvimento de um aplicativo desktop para acompanhamento de álbuns de figurinhas da Copa do Mundo FIFA 2026. O sistema resolve um problema comum entre colecionadores: identificar quais figurinhas já foram obtidas, quais ainda faltam e quais estão repetidas para troca. A aplicação foi desenvolvida em Java, com interface gráfica construída manualmente em Swing, persistência em MySQL e acesso aos dados por JDBC. O ambiente de banco foi isolado com Docker, permitindo que o projeto seja reproduzido em diferentes computadores sem instalação manual do servidor. A arquitetura adotada separa domínio, repositórios, infraestrutura, configuração e interface gráfica, reduzindo dependências entre as partes e mantendo as responsabilidades claras. Também foram aplicados componentes reutilizáveis, seções de tela, tema visual centralizado, carregamento assíncrono com `SwingWorker`, filtros, busca e métricas de acompanhamento da coleção. Como resultado, foi entregue uma aplicação funcional com tela inicial de progresso, álbum completo, controle de quantidade por figurinha e indicadores de figurinhas coletadas, faltantes e repetidas.

#v(4mm)
#text(weight: "bold")[Palavras-chave:] Java. Swing. MySQL. JDBC. Álbum de figurinhas. Docker.

#pagebreak()
#set par(first-line-indent: 1.25cm)
