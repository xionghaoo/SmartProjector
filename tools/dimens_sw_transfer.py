# 根据默认的dimens.xml文件生成对应的sw尺寸文件, 大于1dp才做转换
# 例如：生成values-sw390dp/dimens.xml、value-sw410/dimens.xml文件

import re
import os

target_dp_list = [960]
target_folder_dp_list = [960]

default_dp = 1366

# 通用尺寸范围
SP_START = 1
SP_END = 101
DP_START = 1
DP_END = 1001

# 默认尺寸文件 一般以160dp为单位
res_path = '../app/src/main/res'
values_folder = 'values'
dimens_file = 'dimens.xml'


def create_dimens_line(dimens_sw_file, dp_size):
    # dimens_sw_file.write('\n')
    # create sp line
    dimens_sw_file.write('\t<!--adaptation size start-->\n')
    for sp in range(SP_START, SP_END):
        dimens_sw_file.write('\t<dimen name="_{0}sp">{1}sp</dimen>\n'.format(str(sp), get_dpi_size(sp, dp_size)))
    dimens_sw_file.write('\n')
    # create dp line
    for dp in range(DP_START, DP_END):
        dimens_sw_file.write('\t<dimen name="_{0}dp">{1}dp</dimen>\n'.format(str(dp), get_dpi_size(dp, dp_size)))

    dimens_sw_file.write('\t<!--adaptation size end-->\n')


def create_dimen_file(target_folder_dp, dp_size):
    folder = r'{0}'.format('{0}/values-sw{1}dp'.format(res_path, target_folder_dp))
    if not os.path.exists(folder):
        os.mkdir(folder)

    f = open('{0}/{1}'.format(folder, dimens_file), 'w')

    lines = [line for line in open('{0}/{1}/{2}'.format(res_path, values_folder, dimens_file), 'r')]
    for l in lines:
        sp_line = re.match(r'.*name=["](.*)["]>(.*)sp.*', l)
        if sp_line:
            dimen_name = sp_line.group(1)
            if float(sp_line.group(2)).is_integer() and int(sp_line.group(2)) > 1:
                dimen_value = get_dpi_size(int(sp_line.group(2)), dp_size)
                f.write('\t<dimen name="{0}">{1}sp</dimen>\n'.format(dimen_name, dimen_value))
            else:
                f.write(l)
        else:
            sp_line = re.match(r'.*name=["](.*)["]>(.*)dp.*', l)
            if sp_line:
                # f.write('\n\t<!--dp transfer from default-->\n')
                dimen_name = sp_line.group(1)
                if float(sp_line.group(2)).is_integer() and int(sp_line.group(2)) > 1:
                    dimen_value = get_dpi_size(int(sp_line.group(2)), dp_size)
                    f.write('\t<dimen name="{0}">{1}dp</dimen>\n'.format(dimen_name, dimen_value))
                else:
                    f.write(l)
            else:
                if re.match(r'<resources>', l):
                    f.write(l + '\t<!--sw{0}dp transfer from default-->\n'.format(target_folder_dp))
                # elif re.match(r'</resources>', l):
                #     # 写入dimens line
                #     # create_dimens_line(f, dp_size)
                #     f.write(l)
                else:
                    f.write(l)
    f.close()
    print('transfer file：{0}'.format(f.name))


def get_dpi_size(size, dp_size):
    if dp_size == default_dp:
        return size
    else:
        # 这里的size和dp_size都是dp尺寸，也就是除以缩放因子以后的尺寸
        # size是需要适配的尺寸
        # dp_size是将要生成的sw文件夹基准尺寸
        return round(size * dp_size / default_dp, 2)


def append_default_dimens_file():
    default_f = open('{0}/{1}/{2}'.format(res_path, values_folder, dimens_file), 'r')
    lines = [line for line in default_f]
    default_f.close()
    default_f = open('{0}/{1}/{2}'.format(res_path, values_folder, dimens_file), 'w')
    for l in lines:
        if not re.match(r'.*name=["]_(.*)["]>(.*)[ds]p.*', l):
            if re.match(r'</resources>', l):
                create_dimens_line(default_f, default_dp)
            if not re.match(r'.*<!--adaptation size.*', l):
                default_f.write(l)

    default_f.close()


def create_dp_files():
    if not os.path.exists('{0}/{1}/{2}'.format(res_path, values_folder, dimens_file)):
        print('错误: values/dimens.xml文件不存在')
        return
    append_default_dimens_file()
    for index, dp in enumerate(target_dp_list):
        create_dimen_file(target_folder_dp_list[index], dp)


create_dp_files()


