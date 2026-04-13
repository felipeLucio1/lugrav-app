# Lugrav

## Fluxo de Desenvolvimento

Este projeto foi construído utilizando uma abordagem híbrida de IA e desenvolvimento tradicional, focando na otimização de processos e na adesão às melhores práticas.

A pesquisa e conceituação foi realizada com IAs conversacionais para pesquisa aprofundada de conceitos, práticas recomendadas em arquitetura, uso de bibliotecas e padrões de design. Isso incluiu a consulta a artigos técnicos, documentação oficial do Android e discussões para refinar soluções.

Para o desenvolvimento de UI/UX, um prompt de design detalhado foi elaborado no **Claude**, seguindo os requisitos de UI e as diretrizes do Material 3. Este prompt serviu como base para o **Stich**, que foi utilizado na criação do estilo visual e na prototipagem inicial das telas do aplicativo. O resultado desse processo, um `design.md` conceitual, orientou a elaboração de uma experiência de usuário componentizada no Android Studio.

## Ferramentas Usadas

Este projeto empregou um conjunto de ferramentas para otimizar o processo de desenvolvimento:

**Claude**: Utilizado para brainstorming, planejamento de arquitetura e, notavelmente, para criar prompts de design detalhados que serviram como base para a criação da interface do usuário, seguindo os princípios do Material Design 3.

**Stich**: Ferramenta essencial na fase de design, onde os prompts gerados pelo Claude foram aplicados para a criação do estilo visual do aplicativo e a prototipagem interativa das telas, garantindo uma representação fiel dos requisitos de UI.

**Opencode**: Um assistente de CLI (Command Line Interface) focado em tarefas de engenharia de software, que atuou na geração de código, refatoração e resolução de problemas, agilizando a implementação e manutenção.

**Android Studio**: A IDE (Integrated Development Environment) oficial para o desenvolvimento de aplicativos Android, utilizada para codificação em Kotlin, depuração, construção do projeto e testes em ambiente real.

## Arquitetura e Padrões

A arquitetura da aplicação foi cuidadosamente planejada para garantir escalabilidade, testabilidade e manutenibilidade:

**MVVM com Unidirectional Data Flow**: O ViewModel gerencia o estado da UI e a lógica de negócios, expondo-o para a View. O fluxo de dados é unidirecional, garantindo que o estado flua da fonte única de verdade (ViewModel) para a View, e as ações da View são enviadas de volta para o ViewModel para processamento.

```kotlin
class AudioRecordingViewModel(
    private val repository: AudioRecordingRepository
) : ViewModel() {
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying
}
```

**Repository Pattern**: Uma camada de abstração que isola as fontes de dados (local ou remota) do restante da aplicação. Isso permite que o ViewModel interaja com os dados de forma agnóstica à sua origem, facilitando a troca de implementações de dados e a realização de testes.

**Injeção de Dependência (Koin)**: Utilizada para gerenciar as dependências entre os componentes da aplicação (ViewModels e Repositories). O Koin facilita a criação de módulos, a injeção de instâncias e a garantia de que os componentes recebam suas dependências de forma desacoplada e testável.

## Funcionalidades

O aplicativo Lugrav permite ao usuário realizar as seguintes ações:

- **Gravar Áudio**: Iniciar e finalizar gravações de áudio.
- **Reproduzir Gravações**: Reproduzir, pausar e retomar a reprodução de áudios gravados.
- **Visualizar Gravações**: Listar todas as gravações disponíveis com seus detalhes (nome, duração, data).
- **Gerenciar Permissões**: Solicitar e gerenciar permissões de microfone de forma clara e orientada ao usuário.

### TODOS

- **Adicionar Splash Screen**: Implementar uma tela de abertura que será exibida enquanto a lista de gravações é carregada.
- **Atualizar o Ícone do Aplicativo**: Atualizar o ícone do aplicativo para uma versão personalizada.
- **Compartilhamento de Gravações**: Funcionalidade futura para permitir o compartilhamento de áudios gravados com outros aplicativos ou contatos.

## Melhorias

- **Transcrição via IA**: Funcionalidade futura para transcrever o áudio gravado para texto utilizando inteligência artificial.

## UI/Design

A interface do usuário do Lugrav foi desenvolvida com base nos princípios do Material 3, utilizando componentes reutilizáveis para uma experiência consistente e intuitiva.

Os principais componentes criados são:

- **`LugravTopBar`**: A barra superior do aplicativo, fornecendo navegação e título.

- **`RecordingFAB`**: O Floating Action Button principal para iniciar e parar gravações de áudio.

- **`RecordingCard`**: Um componente que exibe os detalhes de uma gravação específica na lista, permitindo a interação para reprodução.

- **`RecordingBottomSheet`**: Um Modal Bottom Sheet que aparece para controlar a reprodução de um áudio selecionado, incluindo botões de play/pause e tempo de reprodução.

- **`PermissionRationaleDialog`**: Um diálogo que explica a necessidade de permissão do microfone e oferece opções para o usuário (abrir configurações ou cancelar).

## Artigo no Medium

Para uma exploração mais aprofundada da arquitetura, decisões de design e lições aprendidas durante o desenvolvimento do Lugrav, consulte nosso artigo técnico no Medium:

[Link do Artigo - A ser definido]