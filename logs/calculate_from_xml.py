file_path = r"D:\CMS\122b\project\project1\2023-fall-cs122b-hz\test_cases\query_time.log"

flag = False
with open(file_path, 'r') as file:
    file_content = file.read()

    if not file_content.strip().endswith('</log>'):
        flag = True
if flag:
    with open(file_path, 'a') as file:
        file.write('\n</log>')

import xml.etree.ElementTree as ET

# 解析XML文件
tree = ET.parse(file_path)
root = tree.getroot()

# 初始化变量来存储总和和计数
odd_sum = 0
odd_count = 0
even_sum = 0
even_count = 0

# 遍历每个记录
for i, record in enumerate(root.findall('record')):
    # 获取消息值
    message = record.find('message').text
    message_value = float(message)/1000000

    # 根据记录号是奇数还是偶数进行分类求和
    if i % 2 == 0:  # 偶数记录
        even_sum += message_value
        even_count += 1
    else:  # 奇数记录
        odd_sum += message_value
        odd_count += 1

# 计算平均值
odd_avg = (odd_sum-0) / (odd_count-0) if odd_count > 0 else 0
even_avg = (even_sum-0) / (even_count-0) if even_count > 0 else 0

print(odd_count,even_count)
print(odd_sum,even_sum)
print(odd_avg, even_avg)