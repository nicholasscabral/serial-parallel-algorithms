import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

# Carregar os dados do CSV
df = pd.read_csv("results.csv")

# Filtrar os dados para cada algoritmo
merge_sort_data = df[df["Algorithm"] == "MERGE_SORT"]
selection_sort_data = df[df["Algorithm"] == "SELECTION_SORT"]
insertion_sort_data = df[df["Algorithm"] == "INSERTION_SORT"]
bubble_sort_data = df[df["Algorithm"] == "BUBBLE_SORT"]

# Gráfico 1: Comparação do tempo de execução serial entre os algoritmos
def plot_serial_execution_times(data):
    # Calcular a média de tempo de execução para execuções seriais por algoritmo e tamanho de array
    mean_times_serial = data[data['ExecutionType'] == 'Serial'].groupby(['Algorithm', 'DataSize'])['AverageTime'].mean().reset_index()

    # Criar o gráfico de linha para execuções seriais
    plt.figure(figsize=(14, 8))
    lineplot_serial = sns.lineplot(data=mean_times_serial, x='DataSize', y='AverageTime', hue='Algorithm', marker='o')

    plt.title('Average Execution Times for Serial Executions Across Algorithms')
    plt.xlabel('Array Size')
    plt.ylabel('Average Execution Time (ms)')
    plt.legend(title='Algorithm')
    plt.show()

# Gráfico 2: Comparação do tempo médio de execução paralela com o tempo de execução serial para cada algoritmo
def plot_parallel_execution_times_mean(data):
    # Calcular a média de tempo de execução para execuções paralelas por algoritmo e tamanho de array
    mean_times = data[data['ExecutionType'] == 'Parallel'].groupby(['Algorithm', 'DataSize'])['AverageTime'].mean().reset_index()

    # Criar o gráfico de linha para execuções paralelas
    plt.figure(figsize=(14, 8))
    lineplot = sns.lineplot(data=mean_times, x='DataSize', y='AverageTime', hue='Algorithm', marker='o')

    plt.title('Average Execution Times for Parallel Executions Across Algorithms')
    plt.xlabel('Array Size')
    plt.ylabel('Average Execution Time (ms)')
    plt.legend(title='Algorithm')
    plt.show()

# Gráficos 3 a 6: Comparação do tempo de resposta de acordo com a quantidade de núcleo de cada algoritmo
def plot_parallel_execution_times_algorithm(data):
    # Filtrar apenas as execuções paralelas
    parallel_data = data[data['ExecutionType'] == 'Parallel']

    # Definir estilo do gráfico
    sns.set(style="whitegrid")

    # Iterar sobre cada algoritmo único
    for algorithm in parallel_data['Algorithm'].unique():
        # Filtrar dados para o algoritmo atual
        algorithm_data = parallel_data[parallel_data['Algorithm'] == algorithm]
        
        # Criar um gráfico de barras para cada algoritmo
        plt.figure(figsize=(12, 8))
        barplot = sns.barplot(x='DataSize', y='AverageTime', hue='ThreadCount', data=algorithm_data,
                              palette='viridis')
        
        plt.title(f'Execution Times for {algorithm} - Parallel Execution')
        plt.xlabel('Array Size')
        plt.ylabel('Average Execution Time (ms)')
        plt.legend(title='Number of Threads')
        plt.show()


plot_serial_execution_times(df)
plot_parallel_execution_times_algorithm(df)
plot_parallel_execution_times_mean(df)
