= Conclusão

O projeto demonstra que é possível construir um aplicativo desktop funcional e organizado usando Java, Swing, JDBC e MySQL. O sistema atende aos requisitos básicos da disciplina e também aplica decisões de engenharia que tornam o código mais claro e fácil de evoluir.

A separação entre domínio, repositórios, infraestrutura e interface reduziu o acoplamento entre as partes. O uso de Docker tornou o banco reproduzível. O padrão Repository manteve o JDBC controlado e rastreável. O design system em `Theme.java` e os componentes reutilizáveis trouxeram consistência visual sem complexidade excessiva.

A versão atual possui limitações conscientes. O sistema é local, não possui login, não sincroniza dados entre computadores e considera apenas um usuário. Essas restrições mantêm o projeto adequado ao escopo da disciplina e evitam adicionar dependências que desviariam o foco dos conceitos principais.

Ao mesmo tempo, a aplicação ficou mais ambiciosa do que uma entrega mínima. Esse resultado veio de um processo de pesquisa contínua: a cada iteração, o grupo encontrava um problema novo, procurava como ele era resolvido no universo Java e tentava adaptar a solução ao projeto. A principal aprendizagem não foi apenas escrever classes em Java, mas perceber que padrões já conhecidos em outras tecnologias podem ser transportados, com ajustes, para um ecossistema diferente.

Como evoluções naturais, seria possível adicionar múltiplos usuários, suporte a outros álbuns, sincronização em rede, importação de imagens oficiais e troca de figurinhas entre usuários cadastrados. Mesmo sem essas extensões, a aplicação entregue resolve o problema principal: acompanhar a coleção e facilitar a organização das trocas.
