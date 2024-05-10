import pandas as pd
import matplotlib.pyplot as plt

# Carregar os dados do CSV
df = pd.read_csv("results.csv")

# Filtrar os dados para cada algoritmo
merge_sort_data = df[df["Algorithm"] == "MERGE_SORT"]
selection_sort_data = df[df["Algorithm"] == "SELECTION_SORT"]
insertion_sort_data = df[df["Algorithm"] == "INSERTION_SORT"]
bubble_sort_data = df[df["Algorithm"] == "BUBBLE_SORT"]

# Gráfico 1: Comparação do tempo de execução serial entre os algoritmos
plt.figure(figsize=(10, 6))
for algo, data in [
    ("Merge Sort", merge_sort_data),
    ("Selection Sort", selection_sort_data),
    ("Insertion Sort", insertion_sort_data),
    ("Bubble Sort", bubble_sort_data),
]:
    plt.plot(
        data["DataSize"],
        data[data["ExecutionType"] == "Serial"]["AverageTime"],
        label=algo,
    )

plt.xlabel("Tamanho do Array")
plt.ylabel("Tempo de Resposta (ms)")
plt.title("Comparação do tempo de execução serial entre os algoritmos")
plt.legend()
plt.grid(True)
plt.show()

# Gráfico 2: Comparação do tempo médio de execução paralela com o tempo de execução serial para cada algoritmo
plt.figure(figsize=(12, 8))
for algo, data in [
    ("Merge Sort", merge_sort_data),
    ("Selection Sort", selection_sort_data),
    ("Insertion Sort", insertion_sort_data),
    ("Bubble Sort", bubble_sort_data),
]:
    serial_time = data[data["ExecutionType"] == "Serial"]["AverageTime"].iloc[0]
    parallel_data = data[data["ExecutionType"] == "Parallel"]
    parallel_avg_time = parallel_data.groupby("DataSize")["AverageTime"].mean()
    plt.plot(parallel_avg_time.index, parallel_avg_time, label=f"{algo} (Paralelo)")
    plt.axhline(y=serial_time, linestyle="--", color="black", label=f"{algo} (Serial)")

plt.xlabel("Tamanho do Array")
plt.ylabel("Tempo de Resposta (ms)")
plt.title(
    "Comparação do tempo médio de execução paralela com o tempo de execução serial para cada algoritmo"
)
plt.legend()
plt.grid(True)
plt.show()

# Gráfico 3: Comparação do tempo de resposta entre os 4 algoritmos para cada quantidade de núcleos
array_size = 200000
cores = [1, 4, 6, 8]
plt.figure(figsize=(10, 6))
for algo, data in [
    ("Merge Sort", merge_sort_data),
    ("Selection Sort", selection_sort_data),
    ("Insertion Sort", insertion_sort_data),
    ("Bubble Sort", bubble_sort_data),
]:
    avg_times = []
    for core in cores:
        avg_time = data[
            (data["DataSize"] == array_size) & (data["ThreadCount"] == core)
        ]["AverageTime"].iloc[0]
        avg_times.append(avg_time)
    plt.plot(cores, avg_times, marker="o", label=algo)

plt.xlabel("Núcleos")
plt.ylabel("Tempo de Resposta (ms)")
plt.title(
    f"Comparação do tempo de resposta entre os 4 algoritmos para um array de tamanho {array_size}"
)
plt.xticks(cores)
plt.legend()
plt.grid(True)
plt.show()
